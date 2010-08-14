package com.smokejumperit.sublog

import org.codehaus.groovy.transform.*
import org.codehaus.groovy.control.*
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.apache.log4j.Logger

@GroovyASTTransformation(phase=CompilePhase.CONVERSION)
class WithLogImpl implements ASTTransformation {

	def enableLogging = false

	// Cached values
	final ClassNode loggerNode = new ClassNode(
    System.properties["withLog.apacheLogger"] ? Logger : WithLogLogger
  )
	final Class annotationClass = WithLog

  void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
		log("Processing nodes: $nodes")
		ModuleNode fileNode = nodes[0] 

		log("Getting classes")
		def foundClasses = [] as Set
		log("Getting fully qualified classes")
		foundClasses.addAll(fullyQualifiedAnnotationClasses(fileNode))
		log("Getting aliased classes")
		foundClasses.addAll(aliasedAnnotationClasses(fileNode))
		log("Getting package import classes")
		foundClasses.addAll(packageImportAnnotationClasses(fileNode))
		log("Getting the raw annotation classes")
		foundClasses.addAll(rawAnnotationClasses(fileNode))
	
		log("Found $foundClasses in ${sourceUnit.name}")	
		foundClasses.each { ClassNode classNode ->
			log("Adding log to ${classNode.name} of ${sourceUnit.name}")
			classNode.addProperty(
        new PropertyNode(
          "log",
          ClassNode.ACC_PUBLIC | ClassNode.ACC_STATIC | ClassNode.ACC_FINAL,
          loggerNode,
          classNode,
          new StaticMethodCallExpression(loggerNode, "getLogger", new ClassExpression(classNode)),
          null, null
        )
			)
		}
	}

	def findAllWithAnnotation(fileNode, impl) {
		return fileNode.classes?.findAll({ ClassNode classNode -> 
			classNode.annotations?.find { AnnotationNode annotationNode ->
				return impl(annotationNode)
			}
		}) ?: []
	}

	def containsAnnotationPackageImport(fileNode) {
		def pkgName = annotationClass.package.name
		def pkgs = fileNode.importPackages*.toString()
		return [
			"${pkgName}", "${pkgName}.", "${pkgName}.*"
		]*.toString().find { pkgs.contains(it) }
	}

	def rawAnnotationClasses(fileNode) {
		def someImport = fileNode.imports?.find {
			it.type.name.endsWith(".WithLog")
		} || containsAnnotationPackageImport(fileNode)
		def allowRaw = !Boolean.valueOf(System.properties['sublog.withLog.disableRaw'])
		if(!someImport && allowRaw) {
			fileNode.addImport("WithLog", new ClassNode(annotationClass))
			return findAllWithAnnotation(fileNode) {
				it.classNode.name == annotationClass.simpleName
			}
		} else {
			log("Skipping raw annotation: Allowed? $allowRaw. Overriding import? $someImport.")
			return []
		}
	}

	def packageImportAnnotationClasses(fileNode) {
		def isImported = containsAnnotationPackageImport(fileNode)
		if(!isImported) {
			log("Did not find import in: ${fileNode.importPackages}")
			return []
		}
		return findAllWithAnnotation(fileNode) {
			it.classNode.name == annotationClass.simpleName
		}
	}

	def aliasedAnnotationClasses(fileNode) {
		def alias = fileNode.imports?.find { 
			it.type.name == annotationClass.name
		}?.alias
		if(!alias) return []
		return findAllWithAnnotation(fileNode) { 
			it.classNode.name == alias
		}
	}

	def fullyQualifiedAnnotationClasses(fileNode) {
		return findAllWithAnnotation(fileNode) {
			it.classNode.name == annotationClass.name 
		}
	}

	def log(msg) { if(enableLogging) println msg }

}




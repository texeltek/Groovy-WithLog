package com.smokejumperit.sublog

import org.codehaus.groovy.transform.*
import org.codehaus.groovy.control.*
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.apache.log4j.Logger

@GroovyASTTransformation(phase=CompilePhase.CONVERSION)
public class WithLogImpl implements ASTTransformation {

	def enableLogging = false

	// Cached values
	final ClassNode loggerNode = new ClassNode(Logger)
	final Class annotationClass = WithLog

  public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
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
			classNode.addField("log",
				ClassNode.ACC_PRIVATE | ClassNode.ACC_STATIC | ClassNode.ACC_FINAL,
				loggerNode,
				new StaticMethodCallExpression(loggerNode, "getLogger", new ClassExpression(classNode))
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

	def rawAnnotationClasses(fileNode) {
		def someImport = fileNode.imports?.find {
			it.type.name.endsWith(".WithLog")
		}
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
		def pkgName = annotationClass.package.name
		def pkgs = fileNode.importPackages*.toString()
		def isImported = [
			"${pkgName}", "${pkgName}.", "${pkgName}.*"
		]*.toString().find { pkgs.contains(it) }
		if(!isImported) {
			log("Did not find import of $pkgName in: $fileNode.importPackages")
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




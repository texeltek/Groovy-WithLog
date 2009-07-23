package sublog

import org.codehaus.groovy.transform.*
import org.codehaus.groovy.control.*
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.apache.log4j.Logger

@GroovyASTTransformation(phase=CompilePhase.CONVERSION)
public class WithLogImpl implements ASTTransformation {

	// Cached values
	final ClassNode loggerNode = new ClassNode(Logger)
	final Class annotationClass = WithLog

  public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
		ModuleNode fileNode = nodes[1] 

		def foundClasses = [] as Set
		foundClasses.addAll(fullyQualifiedAnnotationClasses(fileNode))
		foundClasses.addAll(aliasedAnnotationClasses(fileNode))
		foundClasses.addAll(packageImportAnnotationClasses(fileNode))
		//foundClasses.addAll(rawAnnotationClasses(fileNode))
		
		foundClasses.each { ClassNode classNode ->
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

	def packageImportAnnotationClasses(fileNode) {
		def isImported = fileNode.importPackages.contains(annotationClass.package.name)
		if(!isImported) return []
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

}




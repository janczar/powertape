package net.janczar.powertape.processor.finder

import net.janczar.powertape.annotation.Inject
import net.janczar.powertape.annotation.InjectProperty
import net.janczar.powertape.annotation.Provide
import net.janczar.powertape.log.Log
import net.janczar.powertape.processor.TypeUtil
import net.janczar.powertape.processor.getName
import net.janczar.powertape.processor.getProviderName
import net.janczar.powertape.processor.model.CodeGraph
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements
import kotlin.reflect.KClass


class CodeFinder(
        private val elements: Elements
) {

    fun searchCode(environment: RoundEnvironment): CodeGraph {
        val codeGraph = CodeGraph()

        val provideAnnotatedConstructors = getProvideAnnotatedConstructors(environment)
        for (annotatedConstructor in provideAnnotatedConstructors) {
            Log.debug("Found @Provide annotated constructor: " + annotatedConstructor.enclosingElement.simpleName)
            if (codeGraph.addProvidedConstructor(annotatedConstructor))
                searchConstructor(annotatedConstructor, codeGraph)
        }

        val injectAnnotatedFields = getInjectAnnotatedFields(environment)
        for (injectedField in injectAnnotatedFields) {
            val type = injectedField.asType() as DeclaredType
            Log.debug("Found @Inject annotated field " + injectedField.simpleName + " of injectedType " + type.asElement().simpleName + " in " + injectedField.enclosingElement.simpleName)
            if (codeGraph.addInjectedField(injectedField))
                searchType(type.asElement() as TypeElement, codeGraph)
        }

        val injectAnnotatedProperties = getInjectAnnotatedProperties(environment)
        for (injectedProperty in injectAnnotatedProperties) {
            val type = injectedProperty.returnType as DeclaredType
            val propertyName = injectedProperty.simpleName.substring(3, 4).toLowerCase() + injectedProperty.simpleName.substring(4)
            Log.debug("Found @InjectProperty annotated property " + propertyName + " of injectedType " + type.asElement().simpleName + " in " + injectedProperty.enclosingElement.simpleName)
            if (codeGraph.addInjectedProperty(injectedProperty))
                searchType(type.asElement() as TypeElement, codeGraph)
        }

        return codeGraph
    }

    private fun getInjectAnnotatedFields(environment: RoundEnvironment): List<VariableElement> {
        return ElementFilter.fieldsIn(environment.getElementsAnnotatedWith(Inject::class.java)).toList()
    }

    private fun getProvideAnnotatedConstructors(environment: RoundEnvironment): List<ExecutableElement> {
        return ElementFilter.constructorsIn(environment.getElementsAnnotatedWith(Provide::class.java)).toList()
    }

    private fun getInjectAnnotatedProperties(environment: RoundEnvironment): List<ExecutableElement> {
        val result = ArrayList<ExecutableElement>()
        environment.rootElements.filter { it.kind == ElementKind.CLASS }.forEach {
            val type = it as TypeElement
            type.enclosedElements.filter { it.kind == ElementKind.METHOD }.forEach{
                if (isProperty(it)) {
                    val propertyName = getPropertyName(it)
                    if (hasAnnotation(type, propertyName, InjectProperty::class)) {
                        result.add(it as ExecutableElement)
                    }
                }
            }
        }
        return result
    }

    private fun searchConstructor(constructor: ExecutableElement, codeGraph: CodeGraph) {
        for (constructorParameter in constructor.parameters) {
            if (constructorParameter.asType().kind == TypeKind.DECLARED) {
                val type = constructorParameter.asType() as DeclaredType
                searchType(type.asElement() as TypeElement, codeGraph)
            }
        }
    }

    private fun searchType(type: TypeElement, codeGraph: CodeGraph) {
        val providerClass = elements.getTypeElement(type.getProviderName())
        if (providerClass != null) {
            Log.debug("Provider for  " + type.getName() + " already exists : "+providerClass.getName()+" "+providerClass.enclosingElement.kind)
            codeGraph.addExistingProvider(type, providerClass)
            return
        }
        for (element in type.enclosedElements) {
            when (element.kind) {
                ElementKind.CONSTRUCTOR -> {
                    val constructor = element as ExecutableElement
                    val provide = constructor.getAnnotation(Provide::class.java)
                    if (provide != null) {
                        Log.debug("Found constructor annotated with @Provide: " + constructor.enclosingElement.simpleName + " in " + type.simpleName)
                        if (codeGraph.addProvidedConstructor(constructor))
                            searchConstructor(constructor, codeGraph)
                    }
                }
                ElementKind.FIELD -> {
                    val field = element as VariableElement
                    val injectAnnotation = field.getAnnotation(Inject::class.java)
                    if (injectAnnotation != null) {
                        if (codeGraph.addInjectedField(field)) {
                            val fieldType = field.asType() as DeclaredType
                            Log.debug("Found injected field " + field.simpleName.toString() + " of injectedType " + fieldType.asElement().simpleName + " in " + type.simpleName)
                            searchType(fieldType.asElement() as TypeElement, codeGraph)
                        }
                    }
                }
                ElementKind.METHOD -> {
                    if (isProperty(element)) {
                        val propertyName = getPropertyName(element)
                        if (hasAnnotation(type, propertyName, InjectProperty::class)) {
                            val property = element as ExecutableElement
                            if (codeGraph.addInjectedProperty(property)) {
                                val fieldType = property.returnType as DeclaredType
                                Log.debug("Found injected property " + propertyName + " of injectedType " + fieldType.asElement().simpleName + " in " + type.simpleName)
                                searchType(fieldType.asElement() as TypeElement, codeGraph)
                            }
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun hasAnnotation(type: TypeElement, propertyName: String, annotation: KClass<*>): Boolean {
        val propertyAnnotationsName = propertyName+"\$annotations"
        for (element in type.enclosedElements) {
            if (element.kind == ElementKind.METHOD && element.simpleName.toString() == propertyAnnotationsName) {
                for (annotationMirror in element.annotationMirrors) {
                    if ((annotationMirror.annotationType.asElement() as TypeElement).qualifiedName.toString() == annotation.qualifiedName) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun isProperty(element: Element) = element.simpleName.startsWith("get")

    private fun getPropertyName(element: Element) = element.simpleName.substring(3,4).toLowerCase() + element.simpleName.substring(4)
}
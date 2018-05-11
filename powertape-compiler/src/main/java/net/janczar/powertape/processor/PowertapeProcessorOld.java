package net.janczar.powertape.processor;


import net.janczar.powertape.annotation.Inject;
import net.janczar.powertape.annotation.Provide;
import net.janczar.powertape.log.Log;
import net.janczar.powertape.processor.finder.CodeFinder;
import net.janczar.powertape.processor.inject.Injectors;
import net.janczar.powertape.processor.provide.Providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"net.janczar.powertape.annotation.Provide", "net.janczar.powertape.annotation.Inject"})
public class PowertapeProcessorOld extends AbstractProcessor {

    private Messager messager;

    private Filer filer;

    private Elements elements;

    private Providers providers = new Providers();

    private Injectors injectors = new Injectors();

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {

//        Log.note("Starting powertape processing!");
//        CodeFinder.Companion.searchCode(env);
//
//        TypeUtil.processingStarted(elements);
//
//        List<ExecutableElement> providedConstructors = new ArrayList<>();
//        List<VariableElement> injectedFields = new ArrayList<>();
//
//        findElements(env, providedConstructors, injectedFields);
//
//        providers.clear();
//        providers.process(providedConstructors);
//
//        injectors.clear();
//        injectors.process(injectedFields);
//
//
//        injectors.resolve(elements, providers);
//        providers.resolve(injectors);
//
//        providers.generateCode(filer);
//        injectors.generateCode(filer);

        return true;
    }

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();

        Log.setMessager(messager);
    }

    private void findElements(final RoundEnvironment env, final List<ExecutableElement> providedConstructors, final List<VariableElement> injectedFields) {
        for (ExecutableElement constructor : ElementFilter.constructorsIn(env.getElementsAnnotatedWith(Provide.class))) {
            Log.note("Found constructor annotated with @Provide: " + constructor.getEnclosingElement().getSimpleName());
            providedConstructors.add(constructor);
            findElementsInConstructor(constructor, providedConstructors, injectedFields, "    ");
        }
        for (VariableElement field : ElementFilter.fieldsIn(env.getElementsAnnotatedWith(Inject.class))) {
            if (!injectedFields.contains(field)) {
                injectedFields.add(field);
            }
            DeclaredType type = (DeclaredType)field.asType();
            Log.note("Found injected field "+field.getSimpleName()+" of injectedType "+type.asElement().getSimpleName()+" in "+field.getEnclosingElement().getSimpleName());
            findElementsInType ( (TypeElement)type.asElement(), providedConstructors, injectedFields, "    ");
        }
    }

    private void findElementsInType(final TypeElement injectedType, final List<ExecutableElement> providedConstructors, final List<VariableElement> injectedFields, String tabs) {
        TypeElement providerClass = elements.getTypeElement(injectedType.getQualifiedName()+"Provider");
        if (providerClass != null) {
            Log.note(tabs+"Provider for  " + injectedType.getQualifiedName()+" already exists.");
            return;
        }
        Log.note(tabs+"There is no provider for  " + injectedType.getQualifiedName());

        for (Element element : injectedType.getEnclosedElements()) {
            if (element.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructor = (ExecutableElement)element;
                Provide provide = constructor.getAnnotation(Provide.class);
                if (provide != null && !providedConstructors.contains(constructor)) {
                    Log.note(tabs+"Found constructor annotated with @Provide for "+injectedType.getQualifiedName());
                    providedConstructors.add(constructor);
                    findElementsInConstructor(constructor, providedConstructors, injectedFields, tabs);
                }
            } else if (element.getKind() == ElementKind.FIELD) {
                VariableElement injectedField = (VariableElement)element;
                Inject injectAnnotation = injectedField.getAnnotation(Inject.class);
                if (injectAnnotation != null) {
                    if (!injectedFields.contains(injectedField)) {
                        injectedFields.add(injectedField);
                    }

                    DeclaredType type = (DeclaredType)injectedField.asType();
                    Log.note(tabs+"Found injected field "+injectedField.getSimpleName()+" of injectedType "+type.asElement().getSimpleName());
                    TypeElement typeElement = (TypeElement)type.asElement();
                    findElementsInType ( typeElement, providedConstructors, injectedFields, tabs + "    ");
                }
            }
        }
    }

    private void findElementsInConstructor(final ExecutableElement constructor, final List<ExecutableElement> providedConstructors, final List<VariableElement> injectedFields, String tabs) {
        for (VariableElement variableElement : constructor.getParameters()) {
            DeclaredType type = (DeclaredType)variableElement.asType();
            Log.note(tabs+"Injected constructor of "+constructor.getEnclosingElement().getSimpleName()+" has field "+variableElement.getSimpleName()+" of injectedType "+type.asElement().getSimpleName());
            findElementsInType ( (TypeElement)type.asElement(), providedConstructors, injectedFields, tabs+"    ");
        }
    }
}

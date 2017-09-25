package net.janczar.powertape.processor;


import net.janczar.powertape.annotation.Inject;
import net.janczar.powertape.annotation.Provide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"net.janczar.powertape.annotation.Provide", "net.janczar.powertape.annotation.Inject"})
public class PowertapeProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {

        Collection<? extends Element> provideElements = env.getElementsAnnotatedWith(Provide.class);
        for (Element element : provideElements) {
            messager.printMessage(Diagnostic.Kind.NOTE, "PROVIDE "+element.getKind()+" "+element.getSimpleName());
        }

        Collection<? extends Element> injectElements = env.getElementsAnnotatedWith(Inject.class);
        for (Element element : injectElements) {
            messager.printMessage(Diagnostic.Kind.NOTE, "INJECT "+element.getKind()+" "+element.getSimpleName());
        }

        return true;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
    }
}

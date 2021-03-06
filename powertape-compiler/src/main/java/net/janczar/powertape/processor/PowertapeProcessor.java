package net.janczar.powertape.processor;


import net.janczar.powertape.annotation.Inject;
import net.janczar.powertape.annotation.Provide;
import net.janczar.powertape.processor.inject.Injectors;
import net.janczar.powertape.processor.provide.Providers;

import java.io.IOException;
import java.io.Writer;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"net.janczar.powertape.annotation.Provide", "net.janczar.powertape.annotation.Inject"})
public class PowertapeProcessor extends AbstractProcessor {

    private Messager messager;

    private Filer filer;

    private Elements elements;

    private Providers providers = new Providers();

    private Injectors injectors = new Injectors();

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {

        TypeUtil.processingStarted(elements);

        providers.clear();
        providers.process(env.getElementsAnnotatedWith(Provide.class));

        injectors.clear();
        injectors.process(env.getElementsAnnotatedWith(Inject.class));
        injectors.resolve(elements, providers);

        providers.resolve(injectors);

        providers.generateCode(filer);
        injectors.generateCode(filer);

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
}

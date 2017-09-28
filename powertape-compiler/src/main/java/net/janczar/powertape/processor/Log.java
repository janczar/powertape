package net.janczar.powertape.processor;


import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class Log {

    private static Messager messager;

    public static void setMessager(final Messager messager) {
        Log.messager = messager;
    }

    public static void note(final String note) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, note);
        }
    }

    public static void error(final String error) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.ERROR, error);
        }
    }

    public static void error(final String error, final Element element) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.ERROR, error, element);
        }
    }
}

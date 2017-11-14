package net.janczar.powertape.log;


import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class Log {

    public static enum LogLevel {
        ERROR,
        INFO
    }

    private static LogLevel logLevel = LogLevel.ERROR;

    private static Messager messager;

    private static Logger logger = new SystemOutLogger();

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

    public static void i(final String tag, final String message) {
        if (logger != null && (logLevel == LogLevel.ERROR || logLevel == LogLevel.INFO)) {
            logger.i(tag, message);
        }
    }

    public static void e(final String tag, final String message) {
        if (logger != null && (logLevel == LogLevel.ERROR)) {
            logger.i(tag, message);
        }
    }

    public static void setLogger(final Logger logger) {
        Log.logger = logger;
    }
}
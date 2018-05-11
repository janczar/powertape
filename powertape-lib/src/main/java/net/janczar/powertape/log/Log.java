package net.janczar.powertape.log;


import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class Log {

    public static enum LogLevel {
        ERROR,
        INFO,
        DEBUG
    }

    private static LogLevel logLevel = LogLevel.DEBUG;

    private static Messager messager;

    private static Logger logger = new SystemOutLogger();

    public static void setMessager(final Messager messager) {
        Log.messager = messager;
    }

    public static void debug(final String note) {
        if (messager != null && (logLevel == LogLevel.DEBUG)) {
            messager.printMessage(Diagnostic.Kind.NOTE, note);
        }
    }

    public static void note(final String note) {
        if (messager != null && (logLevel == LogLevel.DEBUG || logLevel == LogLevel.INFO)) {
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
            messager.printMessage(Diagnostic.Kind.ERROR, element.getSimpleName() + ": " + error);
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

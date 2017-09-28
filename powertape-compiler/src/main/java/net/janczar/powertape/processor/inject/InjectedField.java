package net.janczar.powertape.processor.inject;


import javax.lang.model.element.Element;

public class InjectedField {

    public final Element classElement;

    public final String name;

    public final String className;

    public InjectedField(final Element classElement, final String name, final String className) {
        this.classElement = classElement;
        this.name = name;
        this.className = className;
    }
}

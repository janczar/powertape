package net.janczar.powertape.processor.inject;


import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class InjectedField {

    public final TypeElement containingClass;

    public final String name;

    public final DeclaredType type;

    public final String typeName;

    public InjectedField(final TypeElement containingClass, final String name, final DeclaredType type, final String typeName) {
        this.containingClass = containingClass;
        this.name = name;
        this.type = type;
        this.typeName = typeName;
    }
}

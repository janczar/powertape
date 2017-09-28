package net.janczar.powertape.processor;


import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class TypeUtil {

    public static String getQualifiedName(TypeMirror typeMirror) {
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return null;
        }
        Element declaredTypeElement = ((DeclaredType)typeMirror).asElement();
        if (declaredTypeElement.getKind() != ElementKind.CLASS && declaredTypeElement.getKind() != ElementKind.INTERFACE) {
            return null;
        }
        return ((TypeElement)declaredTypeElement).getQualifiedName().toString();
    }

}

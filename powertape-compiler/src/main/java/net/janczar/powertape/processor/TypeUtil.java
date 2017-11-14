package net.janczar.powertape.processor;


import net.janczar.powertape.annotation.Scope;
import net.janczar.powertape.log.Log;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class TypeUtil {

    private static Elements elements = null;

    public static void processingStarted(final Elements elements) {
        TypeUtil.elements = elements;
    }

    public static void processingEnded() {
        elements = null;
    }

    public static String getQualifiedName(final TypeMirror typeMirror) {
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return null;
        }
        Element declaredTypeElement = ((DeclaredType)typeMirror).asElement();
        if (declaredTypeElement.getKind() != ElementKind.CLASS && declaredTypeElement.getKind() != ElementKind.INTERFACE) {
            return null;
        }
        return ((TypeElement)declaredTypeElement).getQualifiedName().toString();
    }

    public static DeclaredType getScopeClass(final Element element) {

        final String scopeClassName = Scope.class.getName();

        AnnotationValue scopeClass = null;
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(scopeClassName)) {
                for( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet() )
                {
                    if( "value".equals(entry.getKey().getSimpleName().toString() ) )
                    {
                        TypeMirror type = (TypeMirror)entry.getValue().getValue();
                        if (type.getKind() == TypeKind.DECLARED) {
                            return (DeclaredType)type;
                        } else {
                            Log.error("Scope annotation only supports class types!", element);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean sameType(DeclaredType type1, DeclaredType type2) {
        return type1.toString().equals(type2.toString());
    }
}

package net.janczar.powertape.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import kotlin.annotation.AnnotationTarget;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
//@kotlin.annotation.Target(allowedTargets = {AnnotationTarget.FIELD,AnnotationTarget.PROPERTY})
public @interface Inject {
}

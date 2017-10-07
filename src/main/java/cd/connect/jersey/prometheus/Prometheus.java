package cd.connect.jersey.prometheus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a Jersey API as requiring a Histogram wrapper and specifies the metric
 * it will be exposed as and the help provided. This needs to be unique otherwise
 * Prometheus has conniptions.
 *
 * @author Richard Vowles - https://plus.google.com/+RichardVowles
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Prometheus {
	String name() default "";
	String help() default "";
}

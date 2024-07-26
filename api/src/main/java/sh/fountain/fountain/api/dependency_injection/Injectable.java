package sh.fountain.fountain.api.dependency_injection;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an interface or class as injectable thus allowing a DependencyInjector to use it.
 *
 * <p>A concrete class must comply with following contract:</p>
 * <ul>
 *  <li>It is public</li>
 *  <li>It has one public constructor</li>
 *  <li>It's public constructor only has parameter types which are themselves injectable</li>
 * </ul>
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Injectable {
    /**
     * if the annotated classes constructor should automatically be registered as a provider
     *
     * <p>is ignored for non concrete types</p>
     */
    boolean useConstructor() default true;
}

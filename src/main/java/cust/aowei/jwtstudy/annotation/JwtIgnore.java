package cust.aowei.jwtstudy.annotation;

import java.lang.annotation.*;

/**
 * ========================
 * JWT验证忽略注解
 * @author aowei
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JwtIgnore {
//    String value() default "JWT";
}

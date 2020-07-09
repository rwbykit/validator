package rwbykit.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 依赖注解，当需要进行依赖扩展校验时，此注解为扩展的自定义注解的一个属性
 * @author tangxb
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Rely {

    String[] value();

}

package rwbykit.validator.annotation;

import javax.validation.groups.Default;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Cytus_
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Regulated.List.class)
public @interface Regulated {

    /**
     * 表达式
     * @return
     */
    String expression() default "false";

    /**
     * 分组
     * @return
     */
    Class<?>[] groups() default { };

    /**
     * Available constraint annotation list
     * @return
     */
    Available[] availables() default {};

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Available {
        Class<? extends Annotation> annotation();
        Class<?> usefulGroup() default Default.class;
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {

        Regulated[] value();

    }

}

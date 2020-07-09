package rwbykit.validator.util;

import org.springframework.util.CollectionUtils;
import rwbykit.validator.Validator;
import rwbykit.validator.ValidatorFactory;

import javax.validation.ConstraintViolation;
import java.util.Collections;
import java.util.Set;

public class ValidationUtils {

    /**
     * 静态方法调用验证，传入待验证的类示例信息和需要验证的分组，将返回验证未通过的集合输出信息，该信息为Property:Message的一个Set集合
     * @param value 需要验证的类实例信息
     * @param groups 分组
     * @param <T> 验证的泛型类
     * @return 将返回验证未通过的集合输出信息
     */
    final static Validator validator = ValidatorFactory.buildValidatorFactory().getValidator();
    public static <T> Set<String> validator(T value, Class<?>... groups) {
        Set<ConstraintViolation<T>> resultSet = validator.validate(value, groups);
        return CollectionUtils.isEmpty(resultSet) ? Collections.emptySet() :
                Collections.unmodifiableSet(resultSet.stream()
                        .map(ValidationUtils::getConstraintViolationOutMessage)
                        .collect(java.util.stream.Collectors.toSet()));
    }

    /**
     * 将验证结果转换成字符串 格式为Property:Message
     * @param violation
     * @return
     */
    private static String getConstraintViolationOutMessage(ConstraintViolation<?> violation) {
        return new StringBuilder().append(violation.getPropertyPath().toString()).append(":").append(violation.getMessage()).toString();
    }

}

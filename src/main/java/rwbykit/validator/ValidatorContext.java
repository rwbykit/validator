package rwbykit.validator;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.springframework.util.StringUtils;
import rwbykit.validator.annotation.Regulated;
import rwbykit.validator.annotation.Rely;
import rwbykit.validator.groups.ValidationOrder;
import rwbykit.validator.metadata.BeanDescriptor;
import rwbykit.validator.metadata.BeanMetaData;
import rwbykit.validator.metadata.PropertyDescriptor;
import rwbykit.validator.metadata.impl.ConstraintDescriptorImpl;
import rwbykit.validator.util.Collections;
import rwbykit.validator.util.Expressions;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator context object, which is mainly used for verification and returns the verification result
 *
 * @author rwbykits
 */
public class ValidatorContext<T> {

    private final Set<ConstraintViolation<T>> constraintViolations = new HashSet<>();
    private final BeanDescriptor<T> beanDescriptor;
    private final ValidationOrder validationOrder;
    private final ConstraintViolationContext constraintViolationContext;
    private final ConstraintValidatorManager constraintValidatorManager;

    private ValidatorContext(BeanDescriptor<T> beanDescriptor,
                             ValidationOrder validationOrder,
                             ConstraintViolationContext constraintViolationContext,
                             ConstraintValidatorManager constraintValidatorManager) {
        this.beanDescriptor = beanDescriptor;
        this.validationOrder = validationOrder;
        this.constraintViolationContext = constraintViolationContext;
        this.constraintValidatorManager = constraintValidatorManager;
    }

    /**
     * Create a validator context object
     *
     * @param beanMetaData
     * @param validationOrder
     * @param <T>
     * @return Validation context object
     * @see BeanMetaData
     * @see ValidationOrder
     */
    public static <T> ValidatorContext<T> of(BeanMetaData<T> beanMetaData,
                                             ValidationOrder validationOrder,
                                             ConstraintViolationContext constraintViolationContext,
                                             ConstraintValidatorManager constraintValidatorManager) {
        return new ValidatorContext<T>(beanMetaData.getBeanDescriptor(), validationOrder, constraintViolationContext, constraintValidatorManager);
    }

    /**
     * Verify the object value of the current path. If the constraint verification fails, the verification result will be stored
     * in the {@link ValidatorContext#constraintViolations} object
     *
     * @param valueContext Object context information object that currently needs to be verified
     * @param path         Attribute name, support multiple levels of nested incoming, if there is an internal object in the object,
     *                     when configuring the attribute, please use '.' to connect the corresponding nested attribute name
     */
    public <A extends Annotation> void validate(ValueContext<T> valueContext, String path) {
        PropertyDescriptor propertyDescriptor = this.beanDescriptor.getPropertyDescriptor(path);
        Set<ConstraintDescriptor<A>> constraintDescriptors = computeConstraint(valueContext.getCurrentBean(), propertyDescriptor);
        for (ConstraintDescriptor<A> constraintDescriptor : constraintDescriptors) {
            boolean valid = constraintValidate(constraintDescriptor, valueContext, propertyDescriptor);
            if (!valid) {
                assembleViolations(constraintDescriptor, valueContext, path);
            }
        }
    }

    public void validate(ValueContext<T> valueContext) {
        this.beanDescriptor.getPropertyDescriptors().stream()
                .filter(PropertyDescriptor::hasConstraints)
                .map(PropertyDescriptor::getPropertyName)
                .forEach(path -> validate(valueContext, path));
    }


    private <A extends Annotation> void assembleViolations(ConstraintDescriptor<A> constraintDescriptor, ValueContext<T> valueContext, String path) {
        constraintViolations.add(constraintViolationContext.ofConstraintViolation(constraintDescriptor, valueContext, path));
    }

    public Set<ConstraintViolation<T>> getConstraintViolations() {
        return this.constraintViolations;
    }

    /**
     * Calculate the set of constraint annotation descriptions in effect on the current field attributes
     *
     * @param currentObject
     * @param propertyDescriptor
     * @return
     */
    private <A extends Annotation> Set<ConstraintDescriptor<A>> computeConstraint(Object currentObject, PropertyDescriptor propertyDescriptor) {
        if (propertyDescriptor.getRegulatedConstraints().isEmpty()) {
            return computeNonRegulatedConstraint(propertyDescriptor);
        } else {
            return computeRegulatedConstraint(currentObject, propertyDescriptor);
        }
    }

    /**
     * Calculate the constraint verification annotations that need to be executed when the current dependent annotations
     * take effect when the current dependent annotations take effect </br>
     * Special note: the group that currently depends on the annotation is subject to the current execution group,
     * but other constraint verification annotations will not be controlled by the global group execution rules.
     * Whether the execution is managed by the dependent annotation
     *
     * @param currentObject
     * @param propertyDescriptor
     * @return
     * @see Regulated
     * @see Regulated.Available
     */
    @SuppressWarnings("unchecked")
    private <A extends Annotation> Set<ConstraintDescriptor<A>> computeRegulatedConstraint(Object currentObject, PropertyDescriptor propertyDescriptor) {
        // Calculation of effective constraint attributes
        Map<Class<? extends Annotation>, Set<Class<?>>> annotationMap = new HashMap<>(8);
        propertyDescriptor.getRegulatedConstraints().stream()
                .filter(descriptor -> effective(descriptor.getGroups()) && this.computeEffectiveRegulated(currentObject, descriptor))
                .flatMap(descriptor -> Arrays.stream(descriptor.getAnnotation().availables()))
                .forEach(available -> {
                    annotationMap.merge(available.annotation(), Collections.newHashSet(available.usefulGroup()), (oldSet, newSet) -> {
                        newSet.addAll(oldSet);
                        return newSet;
                    });
                });

        if (!annotationMap.isEmpty() && !propertyDescriptor.getConstraints().isEmpty()) {
            return propertyDescriptor.getConstraints().stream()
                    .filter(c -> annotationMap.containsKey(c.getAnnotation().annotationType())
                            && Collections.containsAny(annotationMap.get(c.getAnnotation().annotationType()), (Class<?>[]) c.getAttributes().get(ConstraintHelper.GROUPS)))
                    .map(constraint -> (ConstraintDescriptor<A>) constraint)
                    .collect(Collectors.toSet());
        }
        return java.util.Collections.emptySet();
    }

    /**
     * Calculate the collection information of the current constraint annotation, the interface calculated here is
     * only limited by the grouping in the context
     *
     * @param propertyDescriptor Property descriptor object, Source of calculation data
     * @return The {@link ConstraintDescriptor} set that meets the condition, if there is
     * no satisfaction, it will return an empty set
     */
    @SuppressWarnings("unchecked")
    private <A extends Annotation> Set<ConstraintDescriptor<A>> computeNonRegulatedConstraint(PropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.getConstraints().stream()
                .filter(constraint -> effective(constraint.getGroups()))
                .map(constraint -> (ConstraintDescriptor<A>) constraint)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation, V> Boolean constraintValidate(ConstraintDescriptor<A> constraintDescriptor, ValueContext<T> valueContext, PropertyDescriptor propertyDescriptor) {
        ConstraintValidator<A, V> validator = (ConstraintValidator<A, V>) constraintValidatorManager.getInitializedValidator(propertyDescriptor.getType(),
                (ConstraintDescriptorImpl) constraintDescriptor, constraintValidatorManager.getDefaultConstraintValidatorFactory(), constraintValidatorManager.getDefaultConstraintValidatorInitializationContext());
        validator.initialize(constraintDescriptor.getAnnotation());
        V value = valueContext.getPropertyValue(propertyDescriptor.getPropertyName());
        return constraintValidate(constraintDescriptor, validator, valueContext, value);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private <A extends Annotation, V> boolean constraintValidate(ConstraintDescriptor<A> constraintDescriptor, ConstraintValidator<A, V> validator, ValueContext<T> valueContext, V value) {
        Object relyObject = constraintDescriptor.getAttributes().getOrDefault("source", null);
        if (Objects.nonNull(relyObject) && relyObject.getClass().isAnnotationPresent(Rely.class)) {
            String[] sources = ((Rely) relyObject).value();
            Map.Entry[] entryArray = Arrays.stream(sources)
                    .filter(StringUtils::hasLength)
                    .map(source -> Collections.ofEntry(source, valueContext.getPropertyValue(source)))
                    .toArray(Collections.Entry[]::new);
            Map<String, Object> sourceValueMap = java.util.Collections.unmodifiableMap(Collections.ofHashMap(entryArray));
            RelyConstraintValidator relyConstraintValidator = (RelyConstraintValidator) validator;
            return relyConstraintValidator.isValid(value, sourceValueMap, ConstraintValidatorContextImpl.of(constraintDescriptor));
        } else {
            return validator.isValid(value, ConstraintValidatorContextImpl.of(constraintDescriptor));
        }
    }

    private boolean computeEffectiveRegulated(Object currentObject, ConstraintDescriptor<Regulated> constraintDescriptor) {
        Regulated regulated = constraintDescriptor.getAnnotation();
        String expression = regulated.expression();
        return Expressions.conditionalCalculation(currentObject, expression);
    }

    /**
     * Judging whether the current group information is passing in the attention that needs to be checked,
     * and a match means that the match is successful
     *
     * @param groups Group information configured on field properties
     * @return Is it a valid group
     */
    private boolean effective(Set<Class<?>> groups) {
        return groups.stream().anyMatch(validationOrder::exist);
    }

}

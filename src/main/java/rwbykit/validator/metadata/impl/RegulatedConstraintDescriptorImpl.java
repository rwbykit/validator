package rwbykit.validator.metadata.impl;

import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import rwbykit.validator.annotation.Regulated;

import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ValidateUnwrappedValue;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RegulatedConstraintDescriptorImpl implements ConstraintDescriptor<Regulated> {

    private final Regulated annotation;
    private final Map<String, Object> attributes;

    public RegulatedConstraintDescriptorImpl(Regulated annotation) {
        this.annotation = annotation;
        this.attributes = this.attributes();
    }

    private Map<String, Object> attributes() {
        return AnnotationUtils.getAnnotationAttributes(annotation);
    }

    @Override
    public Regulated getAnnotation() {
        return this.annotation;
    }

    @Override
    public String getMessageTemplate() {
        return ObjectUtils.nullSafeToString(this.attributes.get(ConstraintHelper.MESSAGE));
    }

    @Override
    public Set<Class<?>> getGroups() {
        Class<?>[] classes = (Class<?>[]) this.attributes.get(ConstraintHelper.GROUPS);
        return !ObjectUtils.isEmpty(classes) ?
                Collections.unmodifiableSet(Arrays.stream(classes).collect(Collectors.toSet())) :
                Collections.emptySet();
    }

    @Override
    public Set<Class<? extends Payload>> getPayload() {
        return null;
    }

    @Override
    public ConstraintTarget getValidationAppliesTo() {
        return null;
    }

    @Override
    public List<Class<? extends ConstraintValidator<Regulated, ?>>> getConstraintValidatorClasses() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Set<ConstraintDescriptor<?>> getComposingConstraints() {
        return Collections.emptySet();
    }

    @Override
    public boolean isReportAsSingleViolation() {
        return false;
    }

    @Override
    public ValidateUnwrappedValue getValueUnwrapping() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <U> U unwrap(Class<U> type) {
        throw new UnsupportedOperationException();
    }

}

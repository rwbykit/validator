package rwbykit.validator.metadata.impl;

import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.properties.javabean.JavaBeanField;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.springframework.util.CollectionUtils;
import rwbykit.validator.annotation.Regulated;
import rwbykit.validator.metadata.PropertyDescriptor;

import javax.validation.Constraint;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Cytus_
 */
public class PropertyDescriptorImpl implements PropertyDescriptor {

    private final Type type;
    private final String name;
    private final ConstraintLocation constraintLocation;
    private final Set<ConstraintDescriptor<?>> constraintDescriptors;
    private final Set<ConstraintDescriptor<Regulated>> regulatedConstraintDescriptors;

    PropertyDescriptorImpl(ConstraintLocation constraintLocation, ConstraintHelper constraintHelper) {
        this.type = constraintLocation.getConstrainable().getTypeForValidatorResolution();
        this.name = constraintLocation.getConstrainable().getName();
        this.constraintLocation = constraintLocation;
        List<Annotation> annotations = getActualAnnotations(constraintHelper);
        this.constraintDescriptors = makeConstrainDescriptor(annotations, constraintHelper);
        this.regulatedConstraintDescriptors = makeRegulatedConstrainDescriptor(annotations);
    }

    public static PropertyDescriptorImpl of(ConstraintLocation constraintLocation, ConstraintHelper constraintHelper) {
        return new PropertyDescriptorImpl(constraintLocation, constraintHelper);
    }

    @Override
    public String getPropertyName() {
        return this.name;
    }

    @Override
    public Set<ConstraintDescriptor<?>> getConstraints() {
        return this.constraintDescriptors;
    }

    @Override
    public Set<ConstraintDescriptor<Regulated>> getRegulatedConstraints() {
        return this.regulatedConstraintDescriptors;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public boolean hasConstraints() {
        return !CollectionUtils.isEmpty(constraintDescriptors);
    }

    private Set<ConstraintDescriptor<? extends Annotation>> makeConstrainDescriptor(List<Annotation> annotations,
                                                                                    ConstraintHelper constraintHelper) {
        return CollectionUtils.isEmpty(annotations) ? Collections.emptySet() :
                annotations.stream()
                        .filter(this::isConstraintAnnotation)
                        .map(annotation -> (ConstraintDescriptor<? extends Annotation>) ConstraintDescriptorImpl.of(constraintLocation, annotation, constraintHelper))
                        .collect(Collectors.toSet());
    }


    private Set<ConstraintDescriptor<Regulated>> makeRegulatedConstrainDescriptor(List<Annotation> annotations) {
        return CollectionUtils.isEmpty(annotations) ? Collections.emptySet() :
                annotations.stream()
                        .filter(this::isRegulatedConstraintAnnotation)
                        .map(a -> (Regulated) a)
                        .map(RegulatedConstraintDescriptorImpl::new)
                        .collect(Collectors.toSet());
    }

    private boolean isConstraintAnnotation(Annotation annotation) {
        return annotation.annotationType().isAnnotationPresent(Constraint.class);
    }

    private boolean isRegulatedConstraintAnnotation(Annotation annotation) {
        return Regulated.class.equals(annotation.annotationType());
    }

    private List<Annotation> getActualAnnotations(ConstraintHelper constraintHelper) {
        Annotation[] annotations = ((JavaBeanField) constraintLocation.getConstrainable()).getDeclaredAnnotations();
        List<Annotation> annotationList = new LinkedList<>();
        for (Annotation annotation : annotations) {
            annotationList.addAll(getActualAnnotations(annotation, constraintHelper));
        }
        return annotationList;
    }

    private List<Annotation> getActualAnnotations(Annotation annotation, ConstraintHelper constraintHelper) {
        List<Annotation> annotationList = CollectionHelper.newArrayList(Collections.singletonList(annotation));
        if (constraintHelper.isMultiValueConstraint(annotation.annotationType())) {
            annotationList.addAll(constraintHelper.getConstraintsFromMultiValueConstraint(annotation));
        }
        return annotationList;
    }

}

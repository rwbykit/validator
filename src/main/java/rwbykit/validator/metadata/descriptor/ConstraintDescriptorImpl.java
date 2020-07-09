package rwbykit.validator.metadata.descriptor;

import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;

import java.lang.annotation.Annotation;

public class ConstraintDescriptorImpl<T extends Annotation> extends org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl {

    public ConstraintDescriptorImpl(ConstraintLocation constraintLocation, T annotation, ConstraintHelper constraintHelper) {
        super(constraintHelper, constraintLocation.getConstrainable(), new ConstraintAnnotationDescriptor.Builder(annotation).build(), constraintLocation.getKind());
    }

    public static <T extends Annotation> ConstraintDescriptorImpl<T> of(ConstraintLocation constraintLocation, T annotation, ConstraintHelper constraintHelper) {
        return new ConstraintDescriptorImpl<T>(constraintLocation, annotation, constraintHelper);
    }

}

package rwbykit.validator.groups;

import org.springframework.util.ObjectUtils;

import javax.validation.groups.Default;
import java.util.Collection;
import java.util.Objects;

public class ValidationOrderGenerator {

    /**
     * Generates a order of groups and sequences for the specified validation groups.
     *
     * @param groups the groups specified at the validation call
     *
     * @return an instance of {@code ValidationOrder} defining the order in which validation has to occur
     */
    public ValidationOrder getValidationOrder(Collection<Class<?>> groups) {

        Objects.requireNonNull(groups, "Validation must use group!");

        if ( groups.size() == 1 && groups.contains( Default.class ) ) {
            return ValidationOrder.DEFAULT_GROUP;
        }

        for ( Class<?> clazz : groups ) {
            if (!clazz.isInterface()) {
                throw new IllegalStateException(String.format("Group type[%s] must is interface", clazz.getSimpleName()));
            }
        }

        DefaultValidationOrder validationOrder = new DefaultValidationOrder();
        for ( Class<?> clazz : groups ) {
            if ( Default.class.equals( clazz ) ) {
                validationOrder.insertGroup( Default.class );
            }
            else {
                validationOrder.insertGroup( clazz );
                insertInheritedGroups( clazz, validationOrder );
            }
        }

        return validationOrder;

    }

    private void insertInheritedGroups(Class<?> clazz, DefaultValidationOrder validationOrder) {
        Class<?>[] interfaceClasses = clazz.getInterfaces();
        if (!ObjectUtils.isEmpty(interfaceClasses)) {
            for (Class<?> interfaceClass : interfaceClasses) {
                insertInheritedGroups(interfaceClass, validationOrder);
            }
        }
    }
}

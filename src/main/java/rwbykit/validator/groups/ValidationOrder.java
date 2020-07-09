package rwbykit.validator.groups;

import javax.validation.groups.Default;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface ValidationOrder {

    ValidationOrder DEFAULT_GROUP = new DefaultGroupValidationOrder();

    Collection<Class<?>> groups();

    boolean exist(Class<?> group);

    class DefaultGroupValidationOrder implements ValidationOrder {

        private final List<Class<?>> defaultGroups;

        private DefaultGroupValidationOrder() {
            defaultGroups = Collections.singletonList( Default.class );
        }

        @Override
        public Collection<Class<?>> groups() {
            return defaultGroups;
        }

        @Override
        public boolean exist(Class<?> group) {
            return Default.class.equals(group);
        }
    }

}

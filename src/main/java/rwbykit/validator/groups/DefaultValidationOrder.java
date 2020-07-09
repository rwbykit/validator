package rwbykit.validator.groups;

import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class DefaultValidationOrder implements ValidationOrder {

    private Set<Class<?>> groups;

    public void insertGroup(Class<?> group) {
        if ( groups == null ) {
            groups = new HashSet<>(8);
        }

        groups.add(group);

    }

    @Override
    public Collection<Class<?>> groups() {
        return groups;
    }

    @Override
    public boolean exist(Class<?> group) {
        return !CollectionUtils.isEmpty(groups) && groups.contains(group);
    }
}

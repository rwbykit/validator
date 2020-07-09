package rwbykit.validator.util;

import org.springframework.util.ObjectUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * 流处理工具类
 *
 * @author tangxb
 */
public class Collections {

    public final static <T> BinaryOperator<List<T>> listAddAll() {
        return new ListAddAll<>();
    }

    private static class ListAddAll<T> implements BinaryOperator<List<T>> {

        @Override
        public List<T> apply(List<T> left, List<T> right) {
            left.addAll(right);
            return left;
        }
    }

    public final static <T> BiConsumer<List<T>, T> listAll() {
        return new ListAdd<>();
    }

    private static class ListAdd<T> implements BiConsumer<List<T>, T> {

        @Override
        public void accept(List<T> ts, T t) {
            ts.add(t);
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, A, R> Collector<? super T, A, R> listGroupMap(Function<List<A>, R> finisher) {
        return new CollectorImpl(ArrayList::new, listAll(), listAddAll(), finisher,
                java.util.Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED)));
    }

    static class CollectorImpl<T, A, R> implements Collector<T, A, R> {

        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Function<A, R> finisher,
                      Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }

    }

    public final static <K, V> Map<K, V> ofHashMap(Map.Entry<K, V>... entries) {
        Map<K, V> map = new HashMap<>(8);
        if (Objects.nonNull(entries)) {
            Arrays.stream(entries).forEach(entry -> map.put(entry.getKey(), entry.getValue()));
        }
        return map;
    }

    public final static <K, V> Map.Entry<K, V> ofEntry(K key, V value) {
        return new Entry<>(key, value);
    }

    public static class Entry<K, V> implements Map.Entry<K, V> {

        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    public static <T> T[] newArray(Class<?> componentType, int size) {
        return (T[]) Array.newInstance(componentType, size);
    }

    public static <T> T[] toArray(Collection<T> collection, Class<T> componentType) {
        T[] array = newArray(componentType, collection.size());
        return collection.toArray(array);
    }

    public static <T> boolean containsAny(T[] array, T... ts) {
        return Objects.nonNull(ts) ? Arrays.stream(ts).anyMatch((t) -> ObjectUtils.containsElement(array, t)) : ObjectUtils.containsElement(array, null);
    }

    public static <T> boolean containsAny(Collection<T> collection, T... ts) {
        return containsAny(collection.toArray(), ts);
    }

    public static <T> boolean containsAny(Collection<T> c1, Collection<T> c2) {
        return c2.parallelStream().anyMatch(c -> c1.contains(c));
    }

    public static <T> Set<T> newHashSet(T t) {
        return new HashSet<>(java.util.Collections.singleton(t));
    }

    public static <K, V> Map<K, Set<V>> mapPut(Map<K, Set<V>> valueMap, K key, V value) {
        if (!valueMap.containsKey(key)) {
            valueMap.put(key, newHashSet(value));
        } else {
            Set<V> set = valueMap.get(key);
            set.add(value);
        }
        return valueMap;
    }

}

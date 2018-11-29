package com.infinityrefactoring.util.data;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableNavigableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import com.infinityrefactoring.util.Enumeration;

/**
 * @author Thomás Sousa Silva (ThomasSousa96)
 */
public final class Enums {

    private static Map<Class<?>, Set<? extends Enum<?>>> values;
    private static Map<Class<?>, Map<String, Enum<?>>> valuesByName;
    private static Map<Class<?>, NavigableMap<Integer, Enum<?>>> valuesByOrdinal;
    private static Map<Class<?>, Map<?, Enumeration<?, ?>>> valuesById;
    private static Map<Class<?>, Map<Enum<?>, String>> fullNamesByValue;
    
    public static <E extends Enum<E>> String getFullName(E value) {
        return getFullNamesByValue(value.getDeclaringClass()).get(value);
    }

    public static <E extends Enum<E>> Map<Enum<?>, String> getFullNamesByValue(Class<E> enumClass) {
        if (fullNamesByValue == null) {
            fullNamesByValue = new HashMap<>();
        }
        Map<Enum<?>, String> map = fullNamesByValue.get(enumClass);
        if (map == null) {
            Set<E> values = getValues(enumClass);
            map = values.stream()
                    .collect(toMap(identity(), e -> enumClass.getName().concat(".").concat(e.name()), Streams.throwingMerger(), () -> new LinkedHashMap<>(values.size())));
            fullNamesByValue.put(enumClass, unmodifiableMap(map));
        }
        return map;
    }

    public static <E extends Enum<E>> E getNextValue(E value) {
        return getNextValue(value, false);
    }

    public static <E extends Enum<E>> E getNextValue(E value, boolean cicle) {
        if (value == null) {
            return null;
        }
        int ordinal = value.ordinal();
        NavigableMap<Integer, E> values = getValuesByOrdinal(value.getDeclaringClass());
        if (ordinal == (values.size() - 1)) {
            return (cicle ? values.firstEntry().getValue() : null);
        }
        return values.higherEntry(ordinal).getValue();
    }

    public static <E extends Enum<E>> E getPreviousValue(E value) {
        return getPreviousValue(value, false);
    }
    
    public static <E extends Enum<E>> E getPreviousValue(E value, boolean cicle) {
        if (value == null) {
            return null;
        }
        int ordinal = value.ordinal();
        if ((ordinal == 0) && (!cicle)) {
            return null;
        }
        if (ordinal == 0) {
            return (cicle ? getValuesByOrdinal(value.getDeclaringClass()).lastEntry().getValue() : null);
        }
        return getValuesByOrdinal(value.getDeclaringClass()).lowerEntry(ordinal).getValue();
    }
    
    public static <T extends Serializable, E extends Enum<E> & Enumeration<T, E>> E getValueById(Class<E> enumClass, T id) {
        return getValueById(enumClass, id, true);
    }
    
    public static <T extends Serializable, E extends Enum<E> & Enumeration<T, E>> E getValueById(Class<E> enumClass, T id, boolean required) {
        Map<T, E> values = getValuesById(enumClass);
        E value = values.get(id);
        if (required && (value == null)) {
            throw new NoSuchElementException(String.format("Not found value, from %s class with id: %s", enumClass.getName(), id));
        }
        return value;
    }
    
    public static <E extends Enum<E>> E getValueByName(Class<E> enumClass, String name) {
        return getValueByName(enumClass, name, true);
    }
    
    public static <E extends Enum<E>> E getValueByName(Class<E> enumClass, String name, boolean required) {
        Map<String, E> values = getValuesByName(enumClass);
        E value = values.get(name);
        if (required && (value == null)) {
            throw new NoSuchElementException(String.format("Not found value, from %s class with name: %s", enumClass.getName(), name));
        }
        return value;
    }

    public static <E extends Enum<E>> E getValueByOrdinal(Class<E> enumClass, int ordinal) {
        return getValueByOrdinal(enumClass, ordinal, true);
    }

    public static <E extends Enum<E>> E getValueByOrdinal(Class<E> enumClass, int ordinal, boolean required) {
        Map<Integer, E> values = getValuesByOrdinal(enumClass);
        E value = values.get(ordinal);
        if (required && (value == null)) {
            throw new NoSuchElementException(String.format("Not found value, from %s class with ordinal: %s", enumClass.getName(), ordinal));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> Set<E> getValues(Class<E> enumClass) {
        if (values == null) {
            values = new HashMap<>();
        }
        Set<? extends Enum<?>> set = values.get(enumClass);
        if (set == null) {
            set = unmodifiableSet(EnumSet.allOf(enumClass));
            values.put(enumClass, set);
        }
        return (Set<E>) set;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable, E extends Enum<E> & Enumeration<T, E>> Map<T, E> getValuesById(Class<E> enumClass) {
        if (valuesById == null) {
            valuesById = new HashMap<>();
        }
        Map<?, E> map = (Map<T, E>) (Map<?, ?>) valuesById.get(enumClass);
        if (map == null) {
            Set<E> values = getValues(enumClass);
            map = values.stream()
                    .collect(toMap(e -> e.getId(), identity(), Streams.throwingMerger(), () -> new LinkedHashMap<>(values.size())));
            valuesById.put(enumClass, unmodifiableMap(map));
        }
        return (Map<T, E>) (Map<?, ?>) map;
    }
    
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> Map<String, E> getValuesByName(Class<E> enumClass) {
        if (valuesByName == null) {
            valuesByName = new HashMap<>();
        }
        Map<String, Enum<?>> map = valuesByName.get(enumClass);
        if (map == null) {
            Set<E> values = getValues(enumClass);
            map = values.stream()
                    .collect(toMap(Enum::name, identity(), Streams.throwingMerger(), () -> new LinkedHashMap<>(values.size())));
            valuesByName.put(enumClass, unmodifiableMap(map));
        }
        return (Map<String, E>) map;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> NavigableMap<Integer, E> getValuesByOrdinal(Class<E> enumClass) {
        if (valuesByOrdinal == null) {
            valuesByOrdinal = new HashMap<>();
        }
        NavigableMap<Integer, Enum<?>> map = valuesByOrdinal.get(enumClass);
        if (map == null) {
            Set<E> values = getValues(enumClass);
            map = values.stream()
                    .collect(toMap(Enum::ordinal, identity(), Streams.throwingMerger(), TreeMap::new));
            valuesByOrdinal.put(enumClass, unmodifiableNavigableMap(map));
        }
        return (NavigableMap<Integer, E>) map;
    }

    private Enums() {}
    
}

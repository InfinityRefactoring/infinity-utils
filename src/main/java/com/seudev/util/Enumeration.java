package com.seudev.util;

import java.io.Serializable;

import com.seudev.util.data.Enums;

/**
 * @author Thomás Sousa Silva (ThomasSousa96)
 */
public interface Enumeration<T extends Serializable, E extends Enum<E> & Enumeration<T, E>> extends Identifiable<T>, Named {

    @Override
    @SuppressWarnings("unchecked")
    public default String getNameKey() {
        E value = (E) this;
        return Enums.getFullName(value);
    }
    
    public default E getNextValue() {
        return getNextValue(false);
    }

    @SuppressWarnings("unchecked")
    public default E getNextValue(boolean cicle) {
        E value = (E) this;
        return Enums.getNextValue(value, cicle);
    }

    public default E getPreviousValue() {
        return getPreviousValue(false);
    }
    
    @SuppressWarnings("unchecked")
    public default E getPreviousValue(boolean cicle) {
        E value = (E) this;
        return Enums.getPreviousValue(value, cicle);
    }
    
}

package com.infinityrefactoring.util;

import java.io.Serializable;

/**
 * @author Thomás Sousa Silva (ThomasSousa96)
 */
public interface Identifiable<T extends Serializable> {

    public T getId();
    
}

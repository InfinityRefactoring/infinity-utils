package com.infinityrefactoring.util.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;

import com.seudev.util.Enumeration;
import com.seudev.util.data.Enums;

/**
 * @author Thomás Sousa Silva (ThomasSousa96)
 */
public class EnumsTest {

    @Test
    public void testGetFullNameByValue() {
        String fullName = Enums.getFullName(Foo.B);
        assertEquals(Foo.class.getName().concat(".").concat(Foo.B.name()), fullName);
    }
    
    @Test
    public void testGetNextValue() {
        Foo nextValue = Enums.getNextValue(Foo.C);
        assertNull(nextValue);

        nextValue = Enums.getNextValue(Foo.A);
        assertSame(Foo.B, nextValue);
        
        nextValue = Enums.getNextValue(Foo.C, true);
        assertSame(Foo.A, nextValue);
    }

    @Test
    public void testGetPreviousValue() {
        Foo previousValue = Enums.getPreviousValue(Foo.A);
        assertNull(previousValue);
        
        previousValue = Enums.getPreviousValue(Foo.B);
        assertSame(Foo.A, previousValue);
        
        previousValue = Enums.getPreviousValue(Foo.A, true);
        assertSame(Foo.C, previousValue);
    }
    
    @Test
    public void testGetValueById() {
        Foo a = Enums.getValueById(Foo.class, Foo.A.getId());
        assertSame(Foo.A, a);

        try {
            Enums.getValueById(Foo.class, -1);
            fail();
        } catch (@SuppressWarnings("unused") Exception ex) {
            Foo value = Enums.getValueById(Foo.class, -1, false);
            assertNull(value);
        }
    }
    
    @Test
    public void testGetValueByName() {
        Foo a = Enums.getValueByName(Foo.class, Foo.A.name());
        assertSame(Foo.A, a);
        
        try {
            Enums.getValueByName(Foo.class, "unkwnown name");
            fail();
        } catch (@SuppressWarnings("unused") Exception ex) {
            Foo value = Enums.getValueByName(Foo.class, "unkwnown name", false);
            assertNull(value);
        }
    }
    
    @Test
    public void testGetValueByOrdinal() {
        Foo a = Enums.getValueByOrdinal(Foo.class, Foo.A.ordinal());
        assertSame(Foo.A, a);
        
        try {
            Enums.getValueByOrdinal(Foo.class, -1);
            fail();
        } catch (@SuppressWarnings("unused") Exception ex) {
            Foo value = Enums.getValueByOrdinal(Foo.class, -1, false);
            assertNull(value);
        }
    }

    @Test
    public void testGetValues() {
        Set<Foo> values = Enums.getValues(Foo.class);
        
        assertArrayEquals(Foo.values(), values.toArray(new Foo[0]));
    }
    
    @Test
    public void testGetValuesById() {
        Map<Integer, Foo> map = Enums.getValuesById(Foo.class);

        Foo[] values = map.values().toArray(new Foo[0]);
        assertArrayEquals(Foo.values(), values);

        Integer[] expectedIds = Stream.of(Foo.values()).map(Foo::getId).toArray(Integer[]::new);
        Integer[] ids = map.keySet().toArray(new Integer[0]);

        assertArrayEquals(expectedIds, ids);
    }
    
    @Test
    public void testGetValuesByName() {
        Map<String, Foo> map = Enums.getValuesByName(Foo.class);
        
        Foo[] values = map.values().toArray(new Foo[0]);
        assertArrayEquals(Foo.values(), values);
        
        String[] expectedNames = Stream.of(Foo.values()).map(Foo::name).toArray(String[]::new);
        String[] names = map.keySet().toArray(new String[0]);
        
        assertArrayEquals(expectedNames, names);
    }

    @Test
    public void testGetValuesByOrdinal() {
        Map<Integer, Foo> map = Enums.getValuesByOrdinal(Foo.class);
        
        Foo[] values = map.values().toArray(new Foo[0]);
        assertArrayEquals(Foo.values(), values);
        
        Integer[] expectedOrdinals = Stream.of(Foo.values()).map(Foo::ordinal).toArray(Integer[]::new);
        Integer[] ordinals = map.keySet().toArray(new Integer[0]);
        
        assertArrayEquals(expectedOrdinals, ordinals);
    }

    private static enum Foo implements Enumeration<Integer, Foo> {
        A(10),
        B(20),
        C(30);
        
        private final Integer ID;
        
        private Foo(Integer id) {
            ID = id;
        }
        
        @Override
        public Integer getId() {
            return ID;
        }

    }
    
}

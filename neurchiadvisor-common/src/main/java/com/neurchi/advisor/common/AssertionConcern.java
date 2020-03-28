package com.neurchi.advisor.common;

import java.util.Objects;

public class AssertionConcern {
    
    protected AssertionConcern() {
        
    }

    protected void assertArgumentEquals(Object a, Object b, String message) {
        if (!Objects.equals(a, b)) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentFalse(boolean value, String message) {
        if (value) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentLength(String value, int maximum, String message) {
        int length = value.trim().length();
        if (length > maximum) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentLength(String value, int minimum, int maximum, String message) {
        int length = value.trim().length();
        if (length < minimum || length > maximum) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentNotEmpty(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentNotEquals(Object a, Object b, String message) {
        if (Objects.equals(a, b)) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentRange(double value, double minimum, double maximum, String message) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentRange(float value, float minimum, float maximum, String message) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentRange(int value, int minimum, int maximum, String message) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentRange(long value, long minimum, long maximum, String message) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentTrue(boolean value, String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertStateFalse(boolean value, String message) {
        if (value) {
            throw new IllegalStateException(message);
        }
    }

    protected void assertStateTrue(boolean value, String message) {
        if (!value) {
            throw new IllegalStateException(message);
        }
    }
}

package com.norman.util;

import java.util.Collection;
import java.util.Map;

public class CollectionUtils {
    public CollectionUtils() {
    }

    public static boolean isEmpty(Collection<?> var0) {
        return var0 == null || var0.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> var0) {
        return var0 == null || var0.isEmpty();
    }
}

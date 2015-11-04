package org.respondeco.respondeco.testutil.domain;

/**
 * Created by clemens on 30/09/15.
 */
public class Numerics {

    private static Long currentId = 0L;

    public static Long nextId() {
        currentId = currentId + 1;
        return currentId;
    }

}

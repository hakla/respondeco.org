package org.respondeco.respondeco.testutil;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Created by clemens on 18/11/14.
 */
public class ResultCaptor<T> implements Answer {

    public static <C> ResultCaptor<C> forType(Class<C> clazz) {
        return new ResultCaptor<C>();
    }

    private T value;

    public T getValue() {
        return value;
    }

    @Override
    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        value = (T) invocationOnMock.callRealMethod();
        return value;
    }
}

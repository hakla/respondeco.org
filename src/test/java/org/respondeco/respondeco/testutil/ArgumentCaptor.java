package org.respondeco.respondeco.testutil;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Created by clemens on 18/11/14.
 */
public class ArgumentCaptor<T> implements Answer {

    public static <C> ArgumentCaptor<C> forType(Class<C> clazz, int position) {
        return new ArgumentCaptor<C>(position);
    }

    private int position;
    private T value;

    public ArgumentCaptor(int position) {
        this.position = position;
    }

    public T getValue() {
        return value;
    }

    @Override
    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        value = (T) invocationOnMock.getArguments()[position];
        return invocationOnMock.callRealMethod();
    }
}

package org.respondeco.respondeco.testutil;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Created by clemens on 18/11/14.
 */
public class ArgumentCaptor<T> implements Answer {

    public static <C> ArgumentCaptor<C> forType(Class<C> clazz, int position, boolean callRealMethod) {
        return new ArgumentCaptor<C>(position, callRealMethod);
    }

    private int position;
    private boolean callRealMethod;
    private T value;

    public ArgumentCaptor(int position, boolean callRealMethod) {
        this.position = position;
        this.callRealMethod = callRealMethod;
    }

    public T getValue() {
        return value;
    }

    @Override
    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        value = (T) invocationOnMock.getArguments()[position];
        if(callRealMethod) {
            return invocationOnMock.callRealMethod();
        } else {
            return null;
        }
    }
}

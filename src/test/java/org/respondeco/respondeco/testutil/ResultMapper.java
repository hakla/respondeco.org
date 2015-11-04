package org.respondeco.respondeco.testutil;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.respondeco.respondeco.aop.RESTWrapperAspect;
import org.respondeco.respondeco.web.rest.mapping.ObjectMapperFactory;
import org.springframework.http.HttpStatus;

/**
 * Created by clemens on 19/08/15.
 */
public class ResultMapper implements Answer {

    private RESTWrapperAspect aspect = new RESTWrapperAspect();
    private HttpStatus returnStatus;

    public ResultMapper() {
        aspect.setFactory(new ObjectMapperFactory());
        this.returnStatus = HttpStatus.OK;
    }

    public void setReturnStatus(HttpStatus status) {
        this.returnStatus = status;
    }

    @Override
    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        return aspect.handleInvocation(new RESTWrapperAspect.Invocation() {
            @Override
            public Object invoke() throws Throwable {
                return invocationOnMock.callRealMethod();
            }

            @Override
            public Boolean returnsRawReturnStatus() {
                return false;
            }

            @Override
            public HttpStatus getReturnStatus() {
                return returnStatus;
            }
        });
    }
}

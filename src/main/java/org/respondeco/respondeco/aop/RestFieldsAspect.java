package org.respondeco.respondeco.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.respondeco.respondeco.web.rest.mapper.ObjectMapperFactory;
import org.respondeco.respondeco.web.rest.mapper.ObjectMapperFactoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created by klaus.harrer on 13.04.15.
 */

@Aspect
public class RestFieldsAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private ObjectMapperFactoryProvider factoryProvider;

    @Pointcut("@annotation(org.respondeco.respondeco.aop.WrapWrapWrap)")
    public void restMethodPointcut() {}

    @Around("restMethodPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            factoryProvider.getMapperFactory(result.getClass());
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

            throw e;
        }
    }

}

package org.respondeco.respondeco.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.web.rest.mapper.ObjectMapper;
import org.respondeco.respondeco.web.rest.mapper.ObjectMapperFactoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by klaus.harrer on 13.04.15.
 */

@Aspect
public class RESTWrapperAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private ObjectMapperFactoryProvider factoryProvider;

    @Autowired
    private HttpServletRequest request;

    @Pointcut("@annotation(org.respondeco.respondeco.aop.RESTWrapped)")
    public void wrappedClass() {
    }

    @Pointcut("within(@org.respondeco.respondeco.aop.RESTWrapped *)")
    public void wrappedMethod() {
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Around("publicMethod() && wrappedMethod() || wrappedClass()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            String fields = request.getParameter("fields");

            if (result instanceof Page) {
                Map<String, Object> map = new HashMap<>();
                Page page = (Page) result;
                List content = page.getContent();

                Class<?> clazz = content.get(0).getClass();
                ObjectMapper mapper = factoryProvider.getMapperFactory(clazz).createMapper(fields);

                List<Map<String, Object>> mapped = mapper.mapAll(content);
                String fieldName = clazz.getSimpleName().toLowerCase() + "s";

                map.put("totalItems", ((Page) result).getTotalElements());
                map.put(fieldName, mapped);

                return new ResponseEntity<>(map, HttpStatus.OK);
            } else {
                ObjectMapper mapper = factoryProvider.getMapperFactory(result.getClass()).createMapper(fields);
                return new ResponseEntity<>(mapper.map(result), HttpStatus.OK);
            }
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

            throw e;
        } catch (NoSuchEntityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (OperationForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

}

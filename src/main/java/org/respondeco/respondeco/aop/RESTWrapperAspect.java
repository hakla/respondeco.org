package org.respondeco.respondeco.aop;

import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.respondeco.respondeco.domain.AbstractAuditingEntity;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.web.rest.mapping.ObjectMapper;
import org.respondeco.respondeco.web.rest.mapping.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by klaus.harrer on 13.04.15.
 */

@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
@Setter
public class RESTWrapperAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static interface Invocation {
        public Object invoke() throws Throwable;
        public HttpStatus getReturnStatus();
    }

    @Inject
    private ObjectMapperFactory factory;

    @Inject
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
    public Object wrapAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return handleInvocation(new Invocation() {
            @Override
            public Object invoke() throws Throwable {
                return joinPoint.proceed();
            }

            @Override
            public HttpStatus getReturnStatus() {
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method targetMethod = signature.getMethod();
                RESTWrapped annotation = targetMethod.getAnnotation(RESTWrapped.class);
                return annotation.returnStatus();
            }
        });
    }

    public Object handleInvocation(Invocation invocation) throws Throwable {
        try {
            Object result = invocation.invoke();
            HttpStatus returnStatus = invocation.getReturnStatus();
            log.debug("got value to map: {}", result);
            if(result != null) {
                Object mapped = mapResult(result);
                return new ResponseEntity<>(mapped, returnStatus);
            } else {
                return new ResponseEntity<>(returnStatus);
            }
        } catch (NoSuchEntityException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(e.getObject(), HttpStatus.NOT_FOUND);
        } catch (OperationForbiddenException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(e.getObject(), HttpStatus.FORBIDDEN);
        } catch (IllegalValueException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(e.getObject(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            IllegalValueException.ExceptionObject exceptionObject =
                new IllegalValueException.ExceptionObject(null, e.getMessage());
            return new ResponseEntity<>(exceptionObject, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Object mapResult(Object object) {
        String fields = null;
        if(request != null) {
            fields = request.getParameter("fields");
        }
        if (object instanceof Page) {
            return mapPage((Page) object, fields);
        } else if(object instanceof AbstractAuditingEntity) {
            return mapObject(object, fields);
        } else {
            return object;
        }
    }

    private Map<String, Object> mapPage(Page page, String fields) {
        Map<String, Object> map = new HashMap<>();
        List content = page.getContent();
        if (content.size() > 0) {
            Class<?> clazz = content.get(0).getClass();
            ObjectMapper mapper = factory.createMapper(clazz, fields);
            List<Map<String, Object>> mapped = mapper.mapAll(content);
            String fieldName = clazz.getSimpleName().toLowerCase() + "s";
            map.put(fieldName, mapped);
        }
        map.put("totalItems", page.getTotalElements());
        return map;
    }

    private Map<String, Object> mapObject(Object object, String fields) {
        ObjectMapper mapper = factory.createMapper(object.getClass(), fields);
        return mapper.map(object);
    }

}

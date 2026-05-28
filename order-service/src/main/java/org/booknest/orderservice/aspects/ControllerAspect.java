package org.booknest.orderservice.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ControllerAspect {

    /**
     * Pointcut that matches all methods in the controllers package.
     */
    @Pointcut("execution(* org.booknest.orderservice.controllers..*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        log.info(">>>> [CONTROLLER START] {}.{}() | Args: {}", className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("<<<< [CONTROLLER END] {}.{}() | Duration: {}ms | Result: {}", 
                    className, methodName, duration, result);
            
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("!!!! [CONTROLLER ERROR] {}.{}() | Duration: {}ms | Exception: {} | Message: {}", 
                    className, methodName, duration, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }
}

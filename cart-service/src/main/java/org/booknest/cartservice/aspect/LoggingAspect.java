package org.booknest.cartservice.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* org.booknest.cartservice.controller.*.*(..))")
    public Object logController(ProceedingJoinPoint joint) throws Throwable{

        long start = System.currentTimeMillis();

        log.debug("➡️ [REQUEST]--> {}",joint.getSignature().toShortString());

        Object result=joint.proceed();

        long end = System.currentTimeMillis()-start;

        log.info("✅ [RESPONSE]<-- |Time={}ms", end);

        return result;
    }

    @Around("execution(* org.booknest.cartservice.service.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        long start = System.currentTimeMillis();

        try {
            log.info("🔧 [START] --> {} with params: {}", methodName, Arrays.toString(args));

            Object result = joinPoint.proceed();

            long timeTaken = System.currentTimeMillis() - start;

            log.info("✅ [END] <-- {} returned: {} | Time={}ms",
                    methodName,
                    result,
                    timeTaken);

            return result;

        } catch (Exception ex) {

            long timeTaken = System.currentTimeMillis() - start;

            log.error("❌ [ERROR] in {} with params {} | Time={}ms | Message={}",
                    methodName,
                    Arrays.toString(args),
                    timeTaken,
                    ex.getMessage());

            throw ex;
        }
    }

}

package org.booknest.catelogservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* org.booknest.catelogservice.controllers..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String method = joinPoint.getSignature().toShortString();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String user = (auth != null) ? auth.getName() : "anonymous";

        log.info("REQ  | user={} | {}", user, method);

        try {
            Object result = joinPoint.proceed();

            log.info("RES  | {} | {} ms",
                    method,
                    System.currentTimeMillis() - start);

            return result;

        } catch (Exception ex) {

            log.error("ERR  | {} | {} ms | {}",
                    method,
                    System.currentTimeMillis() - start,
                    ex.getMessage());

            throw ex;
        }
    }

    @Around("execution(* org.booknest.catelogservice.services..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String method = joinPoint.getSignature().toShortString();

        log.debug("SERVICE START | {}", method);

        try {
            Object result = joinPoint.proceed();

            log.debug("SERVICE END   | {} | {} ms",
                    method,
                    System.currentTimeMillis() - start);

            return result;

        } catch (Exception ex) {

            log.error("SERVICE ERR   | {} | {}",
                    method,
                    ex.getMessage());

            throw ex;
        }
    }

    @Around("execution(* org.booknest.catelogservice.repo..*(..))")
    public Object logRepo(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String method = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();

            log.debug("DB | {} | {} ms",
                    method,
                    System.currentTimeMillis() - start);

            return result;

        } catch (Exception ex) {

            log.error("DB ERR | {} | {}",
                    method,
                    ex.getMessage());

            throw ex;
        }
    }

    @Around("execution(* org.booknest.catelogservice.utils.AwsUtils.*(..))")
    public Object logUtils(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String method = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();

            log.debug("UTIL | {} | {} ms",
                    method,
                    System.currentTimeMillis() - start);

            return result;

        } catch (Exception ex) {

            log.error("UTIL ERR | {} | {}",
                    method,
                    ex.getMessage());

            throw ex;
        }
    }
}

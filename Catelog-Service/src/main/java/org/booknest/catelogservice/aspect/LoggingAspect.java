package org.booknest.catelogservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* org.booknest.catelogservice.controllers..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().getName();

        //Get user info from SecurityContext (JWT)
        String user = "ANONYMOUS";
        String roles = "NONE";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            user = auth.getName();
            roles = auth.getAuthorities().toString();
        }

        log.info("➡️ [REQUEST] User={}||Roles={}",
                user, roles);

        try {
            Object result = joinPoint.proceed();

            long timeTaken = System.currentTimeMillis() - start;

            log.info("✅ [RESPONSE] {}|Time={}ms",
                     methodName, timeTaken);
            return result;

        } catch (Exception ex) {
            long timeTaken = System.currentTimeMillis() - start;

            log.error("❌ [ERROR] |{} Time={}ms|Error={}",methodName, timeTaken, ex.getMessage());

            throw ex;
        }
    }

    @Around("execution(* org.booknest.catelogservice.services..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String method = joinPoint.getSignature().toShortString();

        log.info("🔧 [SERVICE-START] || {} || Args={}", method, Arrays.toString(joinPoint.getArgs()));

        Object result = joinPoint.proceed();

        log.info("🔧 [SERVICE-END] || {} Time= || {}ms",
                method, System.currentTimeMillis() - start);

        return result;
    }

    @Around("execution(* org.booknest.catelogservice.repo..*(..))")
    public Object logRepo(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String method = joinPoint.getSignature().toShortString();

        log.debug("🗄️ [DB-CALL] {} || Args={}", method, Arrays.toString(joinPoint.getArgs()));

        Object result = joinPoint.proceed();

        log.debug("🗄️ [DB-RESULT] {} || Time={}ms",
                method, System.currentTimeMillis() - start);

        return result;
    }

    @Around("execution(* org.booknest.catelogservice.utils.AwsUtils.*(..))")
    public Object logUtils(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String method = joinPoint.getSignature().toShortString();

        log.debug("🧩 [UTIL-START] {} Args={}", method, Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();

            log.debug("🧩 [UTIL-END] || {} || Time={}ms",
                    method, System.currentTimeMillis() - start);

            return result;

        } catch (Exception ex) {
            log.error("🧩 [UTIL-ERROR] || {} || Error={}", method, ex.getMessage());
            throw ex;
        }
    }
}

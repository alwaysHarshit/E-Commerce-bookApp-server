package org.booknest.paymentservice.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ErrorAspect {

    /**
     * Pointcut that matches all repositories, services and Web REST controllers.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(org.booknest.paymentservice..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param e exception
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        StringBuilder message = new StringBuilder();
        message.append("Exception in ").append(joinPoint.getSignature().getDeclaringTypeName())
               .append(".").append(joinPoint.getSignature().getName()).append("()");
        
        // Add Request Details if available
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            message.append(" | Request: [").append(request.getMethod()).append("] ").append(request.getRequestURI());
        }

        message.append(" | Cause: ").append(e.getCause() != null ? e.getCause() : "NULL");
        message.append(" | Message: ").append(e.getMessage());

        log.error(message.toString(), e);

        if (log.isDebugEnabled()) {
            log.debug("Arguments passed to {}.{}() : {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs())
            );
        }
    }
}

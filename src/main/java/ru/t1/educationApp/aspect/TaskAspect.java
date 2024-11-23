package ru.t1.educationApp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component

public class TaskAspect {

    @Before("@annotation(LogBefore)")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            log.info("Calling method:{}, arg(s):{}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        } else {
            log.info("Calling method:{}", joinPoint.getSignature().toShortString());
        }

    }

    @AfterThrowing(pointcut = "@annotation(LogAfterThrowing)",
            throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.info("Exception throwing in method:{}, message:{}", joinPoint.getSignature().toShortString(),
                exception.getMessage());
    }

    @AfterReturning(pointcut = "@annotation(LogAfterReturning)",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method:{} returning:{}", joinPoint.getSignature().toShortString(), result);
    }

    @Around("@annotation(LogAround)")
    public Object logAround(ProceedingJoinPoint joinPoint) {
        long start = System.currentTimeMillis();
        log.info("Calling method:{}", joinPoint.getSignature().toShortString());
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception throwing in method:{}, message:{}",
                    joinPoint.getSignature().toShortString(), e.getMessage());
            throw new RuntimeException();
        }
        long finish = System.currentTimeMillis();
        log.info("Execution time [method:{}] in ms:{}", joinPoint.getSignature().toShortString(), finish - start);
        return result;
    }
}

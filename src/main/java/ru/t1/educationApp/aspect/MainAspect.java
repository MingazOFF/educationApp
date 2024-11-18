package ru.t1.educationApp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component

public class MainAspect {

    @Before("@annotation(LogExecution)")
    public void logBefore (JoinPoint joinPoint) {
        log.info("Before calling method: {}",joinPoint.getSignature().getName());
    }

    @Pointcut("execution(public * findById(*)")
    public void exeLog(JoinPoint joinPoint) {
        log.info("Execution: {}", joinPoint.getSignature().getName());
    }
}

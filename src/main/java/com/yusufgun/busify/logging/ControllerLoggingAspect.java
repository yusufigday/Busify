package com.yusufgun.busify.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class ControllerLoggingAspect {

    private final ElasticsearchLogService elasticsearchLogService;
    private final HttpServletRequest request;


    @AfterReturning("execution(public * com.yusufgun.busify.controller.*.*(..))")
    public void logControllerCall(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String httpMethod = request.getMethod();
        String endpoint = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Map<String, Object> details = new HashMap<>();
        details.put("controller", className);
        details.put("method", methodName);
        details.put("httpMethod", httpMethod);
        details.put("endPoint", endpoint);
        details.put("ip", remoteAddr);
        details.put("userAgent", userAgent);

        elasticsearchLogService.sendLog("busify-logs", "INFO",
                httpMethod + " " + endpoint + " -> " + className + "." + methodName, details);

    }

}

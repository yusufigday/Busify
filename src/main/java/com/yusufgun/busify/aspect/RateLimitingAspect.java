package com.yusufgun.busify.aspect;
import com.yusufgun.busify.annotation.RateLimited;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.Duration;
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitingAspect {
    private final RedisTemplate<String, Object> redisTemplate;
    @Around("@annotation(com.yusufgun.busify.annotation.RateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RateLimited rateLimited = signature.getMethod().getAnnotation(RateLimited.class);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = request.getRemoteAddr();
        String methodName = signature.getName();

        String key = "rate_limit:" + ipAddress + ":" + methodName;
        Integer currentCount = (Integer) redisTemplate.opsForValue().get(key);
        if (currentCount == null) {
            redisTemplate.opsForValue().set(key, 1, Duration.ofSeconds(rateLimited.duration()));
        } else if (currentCount < rateLimited.limit()) {
            redisTemplate.opsForValue().increment(key);
        } else {
            throw new IllegalStateException("Çok fazla istek gönderdiniz. Lütfen daha sonra tekrar deneyin.");
        }
        return joinPoint.proceed();
    }
}
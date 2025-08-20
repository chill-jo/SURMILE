package com.example.surveyapp.global.aop;

import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LockAop {

    private final RedissonClient redissonClient;
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Around("@annotation(LockAnnotation)")
    public Object executeLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LockAnnotation lockAnnotation = method.getAnnotation(LockAnnotation.class);

        String lockKeyExpression = lockAnnotation.key();
        String lockKey = parseKey(lockKeyExpression, signature, joinPoint.getArgs());
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(lockAnnotation.waitTime(), lockAnnotation.leaseTime(), lockAnnotation.timeUnit());

            if (!isLocked) {
                throw new CustomException(AopErrorCode.FAILED_GET_LOCK, "락 획득 실패했습니다. : " + lockKey);
            }

            return joinPoint.proceed();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    private String parseKey(String keyExpression, MethodSignature signature, Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 메소드 파라미터 이름과 값을 컨텍스트에 등록
        String[] parameterNames = signature.getParameterNames();

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        // SpEL 표현식 평가
        return expressionParser.parseExpression(keyExpression).getValue(context, String.class);
    }

}

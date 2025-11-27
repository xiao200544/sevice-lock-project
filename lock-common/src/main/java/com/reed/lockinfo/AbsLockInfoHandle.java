package com.reed.lockinfo;

import com.reed.parser.ExtParameterNameDiscoverer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbsLockInfoHandle implements LockInfoHandle {

    private static final String LOCK_DISTRIBUTE_ID_NAME_PREFIX = "LOCK_DISTRIBUTE_ID";

    private final ParameterNameDiscoverer nameDiscoverer = new ExtParameterNameDiscoverer();

    private final ExpressionParser parser = new SpelExpressionParser();

    public static final String SEPARATOR = ":";

    /**
     * ServiceLock(lockType= LockType.Read,name = PROGRAM_SHOW_TIME_LOCK,keys = {"#programId"})
     * 得到锁的前缀，锁的类型：分布式锁和幂等锁
     * @return
     */
    protected abstract String getLockPrefixName();

    /**
     * lockType= LockType.Read,name = PROGRAM_SHOW_TIME_LOCK,keys = {"#programId"}
     * @param joinPoint 切面
     * @param name 锁业务名
     * @param keys 锁
     * @return
     */
    @Override
    public String getLockName(JoinPoint joinPoint, String name, String[] keys) {
        return getLockPrefixName() + SEPARATOR + name + getRelKey(joinPoint, keys);
    }

    private String getRelKey(JoinPoint joinPoint, String[] keys) {
        Method method = getMethod(joinPoint);
        List<String> definitionKeys = getSpElKey(keys, method, joinPoint.getArgs());
        return SEPARATOR + String.join(SEPARATOR, definitionKeys);
    }

    public Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(),
                        method.getParameterTypes()); // 给他方法名字和参数类型，让它找这个方法
            } catch (Exception e) {
                log.error("get method error ",e);
            }
        }
        return method;
    }

    private List<String> getSpElKey(String[] definitionKeys, Method method, Object[] parameterValues) {
        List<String> definitionKeyList = new ArrayList<>();
        for (String definitionKey : definitionKeys) {
            if (!ObjectUtils.isEmpty(definitionKey)) {
                EvaluationContext context = new MethodBasedEvaluationContext(
                        null, method, parameterValues, nameDiscoverer
                );
                Object objKey = parser.parseExpression(definitionKey).getValue(context);
                definitionKeyList.add(ObjectUtils.nullSafeToString(objKey));
            }
        }
        return definitionKeyList;
    }

    /**
     * 得到非注解的分布式锁的名字
     * @param name 锁业务名
     * @param keys 锁
     * @return
     */
    @Override
    public String simpleGetLockName(String name, String[] keys) {
        List<String> list = new ArrayList<>();
        for (String key : keys) {
            if (!key.isEmpty()) {
                list.add(key);
            }
        }
        return LOCK_DISTRIBUTE_ID_NAME_PREFIX + SEPARATOR + name + SEPARATOR + String.join(SEPARATOR, list);
    }
}

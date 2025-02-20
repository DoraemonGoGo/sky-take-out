package com.sky.aspcet;

import com.sky.annotation.AutoFull;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.reflect.MethodSignature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自动填充切面,用于处理公共字段的自动填充
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFull)")
    public void autoFillPointCut() {}

    /**
     * 前置通知
     */
     @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
         log.info("自动填充切面执行");
         // 获取到当前被拦截方法上的数据库操作类型
         MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 获取到方法签名
         AutoFull autoFull = signature.getMethod().getAnnotation(AutoFull.class); // 获取到方法上的注解
         OperationType operationType = autoFull.value(); // 获取到注解的值

         // 获取到拦截的方法的参数--实体对象
         Object[] args = joinPoint.getArgs(); // 获取到方法的参数
         if (args == null || args.length == 0) {
             return;
         }
         Object entity = args[0]; // 获取到第一个参数，即实体对象

         // 准备赋值的数据
         LocalDateTime now = LocalDateTime.now(); // 获取到当前时间
         Long currentId = BaseContext.getCurrentId(); // 获取到当前用户id

         // 根据数据库操作类型，为对应的属性通过反射进行赋值
         if (operationType == OperationType.INSERT) {
             try {
                 Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                 Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                 Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                 Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                 // 通过反射为实体对象的属性赋值
                 setCreateTime.invoke(entity, now);
                 setUpdateTime.invoke(entity, now);
                 setCreateUser.invoke(entity, currentId);
                 setUpdateUser.invoke(entity, currentId);
             }
            catch (Exception e) {
                e.printStackTrace();
            }
         }
         else if (operationType == OperationType.UPDATE) {
             try {
                 Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                 Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                 setUpdateTime.invoke(entity, now);
                 setUpdateUser.invoke(entity, currentId);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
    }
}

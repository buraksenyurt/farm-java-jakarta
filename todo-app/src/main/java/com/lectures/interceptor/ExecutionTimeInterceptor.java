package com.lectures.interceptor;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

/*
LogExecutionTime anotasyonu kullanılan yerlerde çalışacak asıl sınıf
 */
// Bu sınıfın bir interceptor olduğunu belirtiyoruz
@Interceptor
// Hangi anotasyona bağlanacağını belirtiyoruz
@LogExecutionTime
// CDI'a bu interceptor'ü uygulama seviyesinde etkinleştirmesini söylüyoruz
@Priority(Interceptor.Priority.APPLICATION)
public class ExecutionTimeInterceptor {

    @AroundInvoke
    public Object logTime(InvocationContext context) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            return context.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String methodName = context.getMethod().getName();
            // Gerçek hayat senaryosunda bir Loglama alt yapısına çıkılır
            // Burada örnek olarak terminale çıkıyoruz
            System.out.println("EXECUTION DURATIN: `" + methodName + "` duration is " + duration + " milliseconds");
        }
    }
}

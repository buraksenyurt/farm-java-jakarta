package com.lectures.interceptor;

/*
Metotların çalışma zamanlarını ölçmek amaçlı kendi anotasyon türümüz.
 */
import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Bir CDI interceptor olduğunu belirtiyoruz
@InterceptorBinding
// Hem sınıflara hem de metotlara uygulanabileceğini belirtiyoruz
@Target({ElementType.METHOD, ElementType.TYPE})
// Çalışma zamanında okunabilmesini sağlıyoruz
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {

}

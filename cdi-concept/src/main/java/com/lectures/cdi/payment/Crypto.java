package com.lectures.cdi.payment;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;

/*
    Aynı arayüzden türeyen farklı bileşenler olduğunda bunu DI tarafında
ayrıştırmak için Qualifier'lardan yararlanabiliriz. Normalde .NET tarafında
8nci sürüme kadar Factory desenleri ile üstesinden gelirken, 8 sonrası Keyed
Service'ler ile bu işi halleder olduk. Ancak Java tarafında en başından beri
CDI standartları gereği Qualifier'lar ile bunu halledebiliyoruz.

 */
@Qualifier // CDI'a bunun bir Qualifier olduğunu söyler
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface Crypto {
}

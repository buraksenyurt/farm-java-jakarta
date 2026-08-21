package com.lectures.gamecatalog.errorhandling;

import com.lectures.gamecatalog.service.GameNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

/*
Bean Validation hataları veya kendi exception'larımızı fırlatırken, 
bı hatalar varsayılan olarak JAX-RS'in genel HTML hata sayfasına düşer ve
gösterilirler.

Örneğimizde bir servis söz konusu olduğundan JSON formatında yapılandırılmış 
bir çıktıya düşürmek daha mantıklı olacaktır. 

Bu amaçla bir mapper kullanıyor ve GameNotFoundException'ı dönüştürüyoruz.

Benzer işlem validasyon kural ihlallerinde oluşan ConstraintViolationException
türü için ConstraintViolationExceptionMapper sınıfında da yapılıyor.
 */
@Provider
public class GameNotFoundExceptionMapper implements ExceptionMapper<GameNotFoundException> {

    @Override
    public Response toResponse(GameNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
    }

}

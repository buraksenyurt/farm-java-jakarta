package com.lectures.gamecatalog.errorhandling;

import com.lectures.gamecatalog.service.GameBusinessValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

/*
    Normalde GameService sınıfında işlettiğimiz Business Rule kontrol fonksiyonu
exception fırlatıyordu. Bu exception servis katmanında HTTP 500 Internatl Server
error sayfasına yönlendirmeye neden olur. Bu beklenen davranışıdır.

    Eğer aşağıdaki gibi bir Mapper eklersek JAX-RS tarafı bunu ele alır ve
GameBusinessValidationException oluşursa daha güzel bir şekilde bir JSON
çıktısının yansıtılmasını sağlar. JSON Rest standartlarında bir çıktı elde
ederiz.

    Genelde API sınırına kadar ulaşan hiçbir yerde generic JDK exception'ları
(IllegalArgumentException, RuntimeException gibi) fırlatmamak, bunların yerine
kendi domain exception türlerimizi fırlatmak ideal yaklaşımdır.
 */
@Provider
public class GameBusinessValidationExceptionMapper implements ExceptionMapper<GameBusinessValidationException> {

    @Override
    public Response toResponse(GameBusinessValidationException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
    }

}

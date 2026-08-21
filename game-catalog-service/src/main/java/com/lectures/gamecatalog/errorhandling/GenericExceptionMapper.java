package com.lectures.gamecatalog.errorhandling;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    Tanımlamayı unuttuğumuz bir exception tipi söz konusu olursa klasik HTTP
500 sayfasını göstermek yerine alağıdaki mapper ile durumu kontrol altına alıp
kullanıcıya temiz bir 500 JSON içeriği döndürebiliris. Bu sunucu iç detaylarının
(Stack trace, nesne adları vs) dışarıya sızmasını da engeller.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable e) {
        logger.error("Beklenmeyen bir hata oluştu.", e);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Beklenmeyen bir hata oluştu"))
                .build();
    }

}

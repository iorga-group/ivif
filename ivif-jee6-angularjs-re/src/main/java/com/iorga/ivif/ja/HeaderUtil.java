package com.iorga.ivif.ja;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.core.ServerResponse;

import javax.enterprise.context.RequestScoped;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequestScoped
public class HeaderUtil {
    public static final String HEADER_PREFIX = "X-IVIF-JA-";

    private ObjectMapper objectMapper = new ObjectMapper();



    public String toJsonString(Object entity) throws IOException {
        return objectMapper.writeValueAsString(entity);
    }

    public String toBase64JsonString(Object entity) throws IOException {
        return new String(Base64.encodeBase64(toJsonString(entity).getBytes()));
    }

    public void writeTo(Object entity, String headerSuffix, ServerResponse response) throws IOException {
        response.getMetadata().put(HEADER_PREFIX + headerSuffix, (List) Collections.singletonList(toBase64JsonString(entity)));
    }
}

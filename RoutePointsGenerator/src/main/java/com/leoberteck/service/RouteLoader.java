package com.leoberteck.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leoberteck.bean.Route;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;

@Component
public class RouteLoader {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RouteLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<Route> load(String path) throws Exception {
        if(path.startsWith("classpath")){
            try(InputStream in = resourceLoader.getResource(path).getInputStream()){
                var tr = new ParameterizedTypeReference<List<Route>>(){};
                var typeRef = new TypeReference<List<Route>>(){
                    @Override
                    public Type getType() {
                        return tr.getType();
                    }
                };
                return objectMapper.readValue(in, typeRef);
            }
        }
        throw new Exception(path + " the current path schema is not supported for load");
    }
}

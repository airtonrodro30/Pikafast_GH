package com.example.pikafast.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    
    // Se usa para buscar cualquier recurso estatico (mágenes, CSS,etc) 
    // que estan Fuera de las carpetas para recursos estaticas predeterminadas en Spring Bootfuera
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/uploaded-images/**")
                .addResourceLocations("file:uploaded-images/");
        // "file:" indica que es un path en el sistema de archivos
    }
}

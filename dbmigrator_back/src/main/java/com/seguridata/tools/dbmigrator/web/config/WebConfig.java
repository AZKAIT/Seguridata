package com.seguridata.tools.dbmigrator.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableAsync
public class WebConfig implements WebMvcConfigurer {
    private static final String UUID_HEADER = "uuid";

    /**
     * Configuration of CORS.
     *
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600)
                .exposedHeaders(UUID_HEADER);
    }

    static class IndexFallbackResourceResolver extends PathResourceResolver {
        @Override
        protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
                                                   List<? extends Resource> locations, ResourceResolverChain chain) {
            Optional<Resource> resourceOptional = Optional.ofNullable(super.resolveResourceInternal(request, requestPath, locations, chain));

            return resourceOptional
                    .filter(Resource::exists)
                    .orElseGet(() -> super.resolveResourceInternal(request, "/index.html", locations, chain));
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .setOrder(Ordered.LOWEST_PRECEDENCE)
                .addResourceHandler("/ui/**", "/ui", "/ui/")
                .addResourceLocations("classpath:/static/ui/")
                //first time resolved, that route will always be used from cache
                .resourceChain(true)
                .addResolver(new IndexFallbackResourceResolver());
    }
}

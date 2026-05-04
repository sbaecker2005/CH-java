package com.hospitalrafael.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Configura o Spring Boot para servir a SPA React (Vite build) a partir do classpath.
 *
 * Estratégia:
 *  - Recursos estáticos em /static/** são servidos diretamente (JS, CSS, assets)
 *  - Qualquer rota não-API e não-arquivo retorna index.html (SPA fallback)
 *    permitindo que o React Router gerencie a navegação client-side
 *
 * Fluxo:
 *  GET /             → index.html (React app)
 *  GET /leads        → index.html (React Router navega)
 *  GET /assets/x.js  → JS bundle direto
 *  GET /api/**       → Spring controllers (excluídos pelo padrão)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requested = location.createRelative(resourcePath);
                        // Se o arquivo existir (JS, CSS, imagem) → serve diretamente
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        // Caso contrário → retorna index.html para o React Router tratar
                        Resource indexHtml = new ClassPathResource("/static/index.html");
                        return indexHtml.exists() ? indexHtml : null;
                    }
                });
    }
}

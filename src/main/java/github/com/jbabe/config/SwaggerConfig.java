package github.com.jbabe.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@OpenAPIDefinition(info = @Info(title = "JBA-Project", version = "1.0", description = "제주시 농구 협회 프로젝트"))
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String openApiVersion) {
        Info info = new Info()
                .title("JBA Project")
                .version(openApiVersion)
                .description("제주시 농구 협회 프로젝트");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
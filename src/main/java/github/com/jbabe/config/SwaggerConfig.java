package github.com.jbabe.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OperationCustomizer customizer() {
        return (operation, handlerMethod) -> {
            ApiResponses apiResponses = operation.getResponses();
            ApiResponse apiResponse = new ApiResponse()
                    .description("Success")
                    .content(new Content().addMediaType("application/json", new MediaType().schema(new Schema<>().example("{\n  \"code\": 200,\n  \"message\": \"OK\",\n  \"data\": \"데이터는 test로 확인\"\n}"))));
            apiResponses.addApiResponse("200", apiResponse);
            return operation;
        };
    }

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String openApiVersion) {
        Info info = new Info()
                .title("JBA Project")
                .version(openApiVersion)
                .description("제주특별자치도농구협회 홈페이지 프로젝트");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }


}
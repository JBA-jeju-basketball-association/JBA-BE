package github.com.jbabe.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@Getter
public class SwaggerConfig {
//    @Bean
//    public OperationCustomizer customizer() {
//        return (operation, handlerMethod) -> {
//            ApiResponses apiResponses = operation.getResponses();
//            ApiResponse apiResponse = new ApiResponse()
//                    .description("Success")
//                    .content(new Content().addMediaType("application/json", new MediaType().schema(new Schema<>().example("{\n  \"code\": 200,\n  \"message\": \"OK\",\n  \"data\": \"데이터는 test로 확인\"\n}"))));
//            apiResponses.addApiResponse("200", apiResponse);
//            return operation;
//        };
//    }
    @Value("${aws.server-url}")
    private String serverUrl;
    @Value("${aws.new-server-url}")
    private String newServerUrl;


    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String openApiVersion) {

        Info info = new Info()
                .title("JBA Project")
                .version(openApiVersion)
                .description("제주특별자치도농구협회 홈페이지 프로젝트");

        Server server = new Server();
        server.setUrl(serverUrl);
        server.setDescription("Spare Server");

        Server newServer = new Server();
        newServer.setUrl(newServerUrl);
        newServer.setDescription("HTTPS Production Server");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080/");
        localServer.setDescription("Local Server");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("access",
                                new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name("AccessToken"))
                        .addSecuritySchemes("refresh",
                                new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name("RefreshToken"))
                        .addSecuritySchemes("cookie",
                                new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.COOKIE).name("RefreshToken"))
                )
                .info(info)
                .servers(Arrays.asList(newServer, server, localServer))

                .addSecurityItem(new SecurityRequirement().addList("access"))
                .addSecurityItem(new SecurityRequirement().addList("refresh"))
                .addSecurityItem(new SecurityRequirement().addList("cookie"));
//                .addServersItem(new Server().url(serverUrl).description("HTTPS Production Server")) // HTTPS 서버 추가
//                .addServersItem(new Server().url("http://localhost:8080").description("로컬 서버")); // 로컬 서버 추가
    }


}
package gov.va.ascent.starter.swagger.autoconfigure;

import com.fasterxml.classmate.TypeResolver;
import gov.va.ascent.framework.service.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vgadda on 8/3/17.
 */

@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@EnableSwagger2
@ConditionalOnProperty(prefix = "ascent.swagger", name = "enabled", matchIfMissing = true)
@Import({BeanValidatorPluginsConfiguration.class})
public class SwaggerAutoConfiguration {

    private static final String MESSAGE_200 = "A Response which indicates a successful Request.  The Response may contain \"messages\" that could describe warnings or further information.";
    private static final String MESSAGE_403 = "The request is not authorized.  Please verify credentials used in the request.";
    private static final String MESSAGE_400 = "There was an error encountered processing the Request.  Response will contain a  \"messages\" element that will provide further information on the error.  This request shouldn\'t be retried until corrected.";
    private static final String MESSAGE_500 = "There was an error encountered processing the Request.  Response will contain a  \"messages\" element that will provide further information on the error.  Please retry.  If problem persists, please contact support with a copy of the Response.";
    private static final String AUTHORIZATION = "Authorization";
    private static final String SERVICE_RESPONSE = "ServiceResponse";

    @Autowired
    private SwaggerProperties swaggerProperties;

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket categoryApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(swaggerProperties.getGroupName())
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build()
                .ignoredParameterTypes(ApiIgnore.class)
                .additionalModels(typeResolver.resolve(ServiceResponse.class))
                .globalResponseMessage(RequestMethod.GET, globalResponseMessages())
                .globalResponseMessage(RequestMethod.POST, globalResponseMessages())
                .globalResponseMessage(RequestMethod.DELETE, globalResponseMessages())
                .enableUrlTemplating(false)
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion())
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey(AUTHORIZATION, AUTHORIZATION, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(swaggerProperties.getSecurePaths()))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;

        List<SecurityReference> list = new ArrayList<>();
        list.add(new SecurityReference(AUTHORIZATION, authorizationScopes));
        return list;
    }

    private List<ResponseMessage> globalResponseMessages(){
        List<ResponseMessage> responseMessages = new ArrayList<>();
        responseMessages.add(new ResponseMessageBuilder().code(200).message(MESSAGE_200).
                responseModel(new ModelRef(SERVICE_RESPONSE)).build());
        responseMessages.add(new ResponseMessageBuilder().code(400).message(MESSAGE_400).
                responseModel(new ModelRef(SERVICE_RESPONSE)).build());
        responseMessages.add(new ResponseMessageBuilder().code(500).message(MESSAGE_500).
                responseModel(new ModelRef(SERVICE_RESPONSE)).build());
        responseMessages.add(new ResponseMessageBuilder().code(403).message(MESSAGE_403).
                responseModel(new ModelRef(SERVICE_RESPONSE)).build());
        return responseMessages;
    }
}

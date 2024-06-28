//package github.com.jbabe.config;
//
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class WebConfig implements WebFluxConfigurer {
//
//    private final StringToSearchCriteriaEnumConverter stringToSearchCriteriaEnumConverter;
//
//    public WebConfig(StringToSearchCriteriaEnumConverter stringToSearchCriteriaEnumConverter) {
//        this.stringToSearchCriteriaEnumConverter = stringToSearchCriteriaEnumConverter;
//    }
//
//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(stringToSearchCriteriaEnumConverter);
//    }
//}
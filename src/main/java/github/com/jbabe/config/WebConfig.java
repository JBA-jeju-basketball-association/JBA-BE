//package github.com.jbabe.config;
//
//import github.com.jbabe.service.mapper.converter.EnumParamConverter;
//import github.com.jbabe.service.mapper.converter.PutJsonValue;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.format.FormatterRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(new EnumParamConverter<>(PutJsonValue.class));
//    }
//}
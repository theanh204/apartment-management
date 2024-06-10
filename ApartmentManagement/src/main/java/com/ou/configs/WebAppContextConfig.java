/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ou.configs;

import com.ou.formatters.CabinetFormatter;
import com.ou.formatters.FloorFormmatter;
import com.ou.formatters.LocalDateFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;


import java.nio.charset.Charset;

/**
 *
 * @author Admin
 */
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "com.ou.controllers",
        "com.ou.mapper",
        "com.ou.services",
        "com.ou.repositories",
        "com.ou.exception"
})
public class WebAppContextConfig implements WebMvcConfigurer {
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/resources/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("/resources/js/");
    }
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new FloorFormmatter());
        registry.addFormatter(new CabinetFormatter());
        registry.addFormatter(new LocalDateFormatter());
    }


    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        return resolver;
    }


//    public InternalResourceViewResolver internalResourceViewResolver() {
//        InternalResourceViewResolver resolver  = new InternalResourceViewResolver();
//        // Configure the view class to be used as JSTL for rendering views
//        resolver .setViewClass(JstlView.class);
//        resolver.setPrefix("/WEB-INF/pages/"); // Tiền tố
//        resolver.setSuffix(".jsp"); // Hậu tố
//        return resolver;
//    }
}

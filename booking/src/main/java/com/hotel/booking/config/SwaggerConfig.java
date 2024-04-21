package com.hotel.booking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {


    /**
     * The function `customOpenAPIConfig` creates a custom OpenAPI configuration for a Hotel Booking
     * Service Swagger interface.
     *
     * @return An OpenAPI object is being returned with custom configuration for a Hotel Booking
     * Service API. The OpenAPI object includes information such as version, title, description, and
     * contact details for the API.
     */
    @Bean
    public OpenAPI customOpenAPIConfig() {

        return new OpenAPI()
                .info(new Info()
                        .version("0.0.1")
                        .title("Hotel Booking Service")
                        .description("This Swagger Interface details out the APIs for Assessment Hotel Booking Service")
                        .contact(new Contact().name("HRS group").url("http://www.dummycontact.dcom/")));
    }
}

package com.starling.account.transactions.roundup.saving.client.starling.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * @author shahbazhussain
 */
@Configuration
public class RestClientConfiguration {

    @Bean
    RestClient restClient() {
        return RestClient.create();
    }
}

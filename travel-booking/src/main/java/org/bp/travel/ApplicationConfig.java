package org.bp.travel;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.saga.InMemorySagaService;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {


    @Bean
    public CamelContextConfiguration integrationContextMetadataConfiguration() {
        return new CamelContextConfiguration() {
 
            @Override
            public void beforeApplicationStart(CamelContext camelContext) {
			    try {
					camelContext.addService(new InMemorySagaService());
					camelContext.setMessageHistory(true);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
            }
 
            @Override
            public void afterApplicationStart(CamelContext camelContext) {
                // nop
            }
        };
    }	

}
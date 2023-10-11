package org.bp.paymentclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import org.bp.payment.model.Amount;
import org.bp.payment.model.PaymentCard;
import org.bp.payment.model.PaymentRequest;
import org.bp.payment.model.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class PaymentClientApplication {

	private static final Logger log = LoggerFactory.getLogger(PaymentClientApplication.class);
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			PaymentRequest pr = new PaymentRequest();
			PaymentCard pc = new PaymentCard();
			pr.setPaymentCard(pc);
			pc.setName("JJ"); 
			pc.setNumber("999"); 
			pc.setValidTo("10/50");
			
			Amount a = new Amount();
			pr.setAmount(a);
			a.setValue(new BigDecimal(1000)); 
			a.setCurrency("PLN");
			
			try {
				ResponseEntity<PaymentResponse> re = restTemplate.postForEntity(
					"http://localhost:8083/payment", 
					pr, 
					PaymentResponse.class
				);
				log.info(re.getBody().getTransactionDate().toString() + " -> " + Integer.toString(re.getBody().getTransactionId()));
			}
			catch (HttpClientErrorException e) { //catch 4xx response codes
				log.error(e.getResponseBodyAsString());
			}
		};
	}
	
	public static void main(String[] args) {
		SpringApplication.run(PaymentClientApplication.class, args);
	}

}

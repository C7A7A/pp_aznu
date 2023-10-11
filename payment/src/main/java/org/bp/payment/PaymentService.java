package org.bp.payment;
import java.math.BigDecimal;
import java.util.Date;

import org.bp.payment.model.PaymentException;
import org.bp.payment.model.PaymentRequest;
import org.bp.payment.model.PaymentResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class PaymentService {
	
	
		@PostMapping("/payment")
		@Operation(
			summary = "payment operation",
			description = "operation for payment",
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "OK",
					content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResponse.class))
					}
				),
				@ApiResponse(
					responseCode = "400", 
					description = "Bad Request",
					content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
					}
				)
			}
		)
		public PaymentResponse payment(@RequestBody PaymentRequest paymentRequest) {
			if (paymentRequest!=null && paymentRequest.getAmount()!=null 
					&& paymentRequest.getAmount().getValue()!=null
					&& paymentRequest.getAmount().getValue().compareTo(new BigDecimal(0))<0) {

				throw new PaymentException("Amount value can not negative");
				
			}
				
			PaymentResponse paymentResponse = new PaymentResponse();
			paymentResponse.setTransactionDate(new Date());
			paymentResponse.setTransactionId(999);
			return paymentResponse;
		}
		
		@GetMapping("payment/{id}")
		public PaymentResponse getPayment(@PathVariable int id) {
			PaymentResponse paymentResponse = new PaymentResponse();
			paymentResponse.setTransactionDate(new Date());
			paymentResponse.setTransactionId(id);
			
			return paymentResponse;
		}

}

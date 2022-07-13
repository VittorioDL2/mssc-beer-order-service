package guru.sfg.beer.order.service.services.listeners;

import java.util.UUID;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import guru.sfg.brewery.model.events.ValidateOrderResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationResultListener {

	private final BeerOrderManager beerOrderManager;
	
	@Transactional
	@JmsListener(destination=JmsConfig.VALIDATE_ORDER_RESULT_QUEUE)
	public void listen(ValidateOrderResult event) {
		UUID beerId = event.getOrderId();
		
		Boolean isValid = event.getIsValid();
		
		log.debug("validated beer " + beerId + " VALID : " + isValid);
		
		beerOrderManager.processValidationResult(beerId, isValid);
	}
}

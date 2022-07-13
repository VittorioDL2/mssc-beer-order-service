package guru.sfg.beer.order.service.services.listeners;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import guru.sfg.brewery.model.events.AllocateOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationResultListener {

private final BeerOrderManager beerOrderManager;
	
	@Transactional
	@JmsListener(destination=JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE)
	public void listen(AllocateOrderResponse event) {
		if(event.getIsValid() == false && !event.getPendingInventory()) {
			beerOrderManager.beerOrderAllocationPassed(event.getBeerOrder());
		}else if(event.getIsValid() && event.getPendingInventory()) {
			beerOrderManager.beerOrderAllocationPendingInventory(event.getBeerOrder());
		} else {
			beerOrderManager.beerOrderAllocationFailed(event.getBeerOrder());
		}
	}
}

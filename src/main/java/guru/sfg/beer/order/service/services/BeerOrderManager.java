package guru.sfg.beer.order.service.services;

import java.util.UUID;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.brewery.model.BeerOrderDto;

public interface BeerOrderManager {
	
	
	BeerOrder newBeerOrder(BeerOrder beerOrder);

	void processValidationResult(UUID beerId, Boolean isValid);
	
	void beerOrderAllocationPassed(BeerOrderDto beerOrder);
	
	void beerOrderAllocationPendingInventory(BeerOrderDto beerOrder);
	
	void beerOrderAllocationFailed(BeerOrderDto beerOrder);
	
	void beerOrderPickedUp(UUID orderId);
	
	void cancelOrder(UUID orderId);
}

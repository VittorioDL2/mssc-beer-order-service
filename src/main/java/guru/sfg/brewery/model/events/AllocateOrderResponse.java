package guru.sfg.brewery.model.events;

import java.util.UUID;

import guru.sfg.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocateOrderResponse {

	private BeerOrderDto beerOrder;
	private Boolean isValid = false;
	private Boolean pendingInventory = false;
	
}

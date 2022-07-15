package guru.sfg.beer.order.service.stateMachine.actions;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import guru.sfg.beer.order.service.web.mappers.BeerOrderMapper;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.events.AllocateOrderRequest;
import guru.sfg.brewery.model.events.DeallocateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeallocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum>{

	private final BeerOrderRepository repository;
	private final BeerOrderMapper mapper;
	private final JmsTemplate jmsTemplate;

	@Override
	public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
		String beerOrderId = (String) context.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);
		Optional<BeerOrder> orderOpt = repository.findById(UUID.fromString(beerOrderId));
		
		orderOpt.ifPresentOrElse(order -> {
			BeerOrderDto dto = mapper.beerOrderToDto(order);
			jmsTemplate
			.convertAndSend(JmsConfig.DEALLOCATE_ORDER_QUEUE, DeallocateOrderRequest.builder().beerOrder(dto).build());
		
		log.info("deallocate request sent");
		}, () -> log.error("not present"));
		
		
	}

}
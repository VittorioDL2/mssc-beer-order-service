package guru.sfg.beer.order.service.services;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.stateMachine.BeerOrderStateChangedInterceptor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {
	
	public static final String ORDER_ID_HEADER = "order-id";
	private final BeerOrderRepository beerOrderRepository;
	private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> smf;
	private final BeerOrderStateChangedInterceptor interceptor;
	
	@Transactional
	@Override
	public BeerOrder newBeerOrder(BeerOrder beerOrder) {
		beerOrder.setId(null);
		beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
		
		BeerOrder saved = beerOrderRepository.save(beerOrder);
		
		sendBeerOrderEvent(saved, BeerOrderEventEnum.VALIDATE_ORDER);
		
		return saved;
	}

	
	private void sendBeerOrderEvent(BeerOrder order, BeerOrderEventEnum event) {
		StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = build(order);
		
		Message msg = MessageBuilder.withPayload(event)
				.setHeader(ORDER_ID_HEADER, order.getId().toString())
				.build();
		
		sm.sendEvent(msg);
	}
	
	private StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder order){
		StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = smf.getStateMachine(order.getId());
		
		sm.stop();
		
		sm.getStateMachineAccessor()
			.doWithAllRegions(sma -> {
				sma.addStateMachineInterceptor(interceptor);
				sma.resetStateMachine(new DefaultStateMachineContext<>(order.getOrderStatus(),null,null,null));
			});
		
		sm.start();
		
		return sm;
	}
}

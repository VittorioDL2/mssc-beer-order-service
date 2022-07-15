package guru.sfg.beer.order.service.services;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.sound.sampled.Line;

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
import guru.sfg.brewery.model.BeerOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {
	
	public static final String ORDER_ID_HEADER = "order-id";
	private final BeerOrderRepository beerOrderRepository;
	private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> smf;
	private final BeerOrderStateChangedInterceptor interceptor;
	private final EntityManager entityManager;
	
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

	@Transactional
	@Override
	public void processValidationResult(UUID beerId, Boolean isValid) {
		entityManager.flush();
		
		beerOrderRepository.findById(beerId).ifPresentOrElse(order -> {
			if(isValid) {
				sendBeerOrderEvent(order,BeerOrderEventEnum.VALIDATION_PASSED);
				
				BeerOrder validatedOrder = beerOrderRepository.findOneById(beerId);
				
				sendBeerOrderEvent(validatedOrder, BeerOrderEventEnum.ALLOCATE_ORDER);
			} else {
				sendBeerOrderEvent(order,BeerOrderEventEnum.VALIDATION_FAILED);
			}
		}, () -> log.debug("not present"));
	}


	@Override
	public void beerOrderAllocationPassed(BeerOrderDto beerOrder) {
		BeerOrder order = beerOrderRepository.findOneById(beerOrder.getId());
		
		sendBeerOrderEvent(order, BeerOrderEventEnum.ALLOCATION_SUCCESS);
		
		updateAllocatedQty(beerOrder, order);
	}

	private void awaitForStatus(UUID orderId, BeerOrderStatusEnum statusEnum) {
		AtomicInteger loopCount = new AtomicInteger(0);
		AtomicBoolean found = new AtomicBoolean(false);
		
		while(!found.get()) {
			if(loopCount.incrementAndGet() > 10) {
				found.set(true);
				log.debug("retries exceeded");
			}
			
			beerOrderRepository.findById(orderId)
				.ifPresentOrElse(order -> {
					if(order.getOrderStatus().equals(statusEnum)) {
						found.set(true);
						log.debug("found");
					}
				}, () -> log.debug("ORDER ID NOT FOUND"));
			
			if(!found.get()) {
				try {
					log.debug("sleeping for retry");
					Thread.sleep(100);
				} catch(Exception e) {
					
				}
			}
		}
	}
	
	private void updateAllocatedQty(BeerOrderDto dto, BeerOrder order) {
		BeerOrder allocated = beerOrderRepository.findOneById(dto.getId());
		
		allocated.getBeerOrderLines().forEach(line -> {
			dto.getBeerOrderLines().forEach(dtoLine -> {
				if(dtoLine.getBeerId().equals(line.getId())) {
					line.setQuantityAllocated(dtoLine.getQuantityAllocated());
				}
			});
		});
		
		beerOrderRepository.saveAndFlush(order);
	}
	
	
	@Override
	public void beerOrderAllocationPendingInventory(BeerOrderDto beerOrder) {
		BeerOrder order = beerOrderRepository.findOneById(beerOrder.getId());
		
		sendBeerOrderEvent(order, BeerOrderEventEnum.ALLOCATION_NO_INVENTORY);
		
		updateAllocatedQty(beerOrder, order);
	}


	@Override
	public void beerOrderAllocationFailed(BeerOrderDto beerOrder) {
		beerOrderRepository.findById(beerOrder.getId()).ifPresentOrElse(order -> sendBeerOrderEvent(order, BeerOrderEventEnum.ALLOCATION_FAILED), () -> log.debug("not present"));
		
	}


	@Override
	public void beerOrderPickedUp(UUID orderId) {
		Optional<BeerOrder> orderOpt = beerOrderRepository.findById(orderId);
		orderOpt.ifPresentOrElse(order -> {
			sendBeerOrderEvent(order, BeerOrderEventEnum.BEERORDER_PICKED_UP);
		}, 
				() -> log.debug("not present"));
	}


	@Override
	public void cancelOrder(UUID orderId) {
		Optional<BeerOrder> orderOpt = beerOrderRepository.findById(orderId);
		orderOpt.ifPresentOrElse(order -> {
			sendBeerOrderEvent(order, BeerOrderEventEnum.CANCEL_ORDER);
		}, 
				() -> log.debug("not present"));
	}
}

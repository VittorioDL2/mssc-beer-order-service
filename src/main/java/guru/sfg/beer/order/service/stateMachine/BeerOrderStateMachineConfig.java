package guru.sfg.beer.order.service.stateMachine;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableStateMachineFactory
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {@Override
	public void configure(StateMachineConfigurationConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> config)
			throws Exception {
		// TODO Auto-generated method stub
		super.configure(config);
	}

	@Override
	public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states)
			throws Exception {
		states.withStates()
			.initial(BeerOrderStatusEnum.NEW)
			.states(EnumSet.allOf(BeerOrderStatusEnum.class))
			.end(BeerOrderStatusEnum.DELIVERY_EXCEPTION)
			.end(BeerOrderStatusEnum.DELIVERED)
			.end(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
			.end(BeerOrderStatusEnum.PICKED_UP)
			.end(BeerOrderStatusEnum.ALLOCATION_EXCEPTION);
			
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> transitions)
			throws Exception {
		// TODO Auto-generated method stub
		super.configure(transitions);
	}


}

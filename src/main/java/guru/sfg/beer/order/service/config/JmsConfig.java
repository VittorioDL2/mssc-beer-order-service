package guru.sfg.beer.order.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JmsConfig {

	public static final String VALIDATE_ORDER_QUEUE = "validate-order";
	public static final String ALLOCATE_ORDER_QUEUE = "allocate-order";

	public static final String ALLOCATE_ORDER_RESULT_QUEUE = "allocate-order-result";
	public static final String VALIDATE_ORDER_RESULT_QUEUE = "validate-order-result";
	
	public static final String ALLOCATE_FAILURE_QUEUE = "allocate-order-failure";
	
	@Bean
	public MessageConverter jacksonJmsMessageConverter(ObjectMapper mapper) {
		MappingJackson2MessageConverter mj2mc = new MappingJackson2MessageConverter();
		mj2mc.setTargetType(MessageType.TEXT);
		mj2mc.setObjectMapper(mapper);
		mj2mc.setTypeIdPropertyName("_type");
		return mj2mc;
	}

}

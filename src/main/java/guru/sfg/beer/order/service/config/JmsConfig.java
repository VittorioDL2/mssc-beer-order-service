package guru.sfg.beer.order.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {

	public static final String VALIDATE_ORDER_QUEUE = "validate-order";
	
	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter mj2mc = new MappingJackson2MessageConverter();
		mj2mc.setTargetType(MessageType.TEXT);
		mj2mc.setTypeIdPropertyName("_type");
		return mj2mc;
	}

}

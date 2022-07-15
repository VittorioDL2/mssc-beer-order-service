package guru.sfg.beer.order.service.web.mappers;

import org.mapstruct.Mapper;

import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.brewery.model.CustomerDto;

@Mapper(uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface CustomerMapper {
	CustomerDto customerToDto(Customer customer);
	Customer DtoToCustomer(CustomerDto dto);
}

package guru.sfg.beer.order.service.services;

import org.springframework.data.domain.PageRequest;

import guru.sfg.brewery.model.CustomerPagedList;

public interface CustomerService {

	CustomerPagedList listCustomers(PageRequest of);

}

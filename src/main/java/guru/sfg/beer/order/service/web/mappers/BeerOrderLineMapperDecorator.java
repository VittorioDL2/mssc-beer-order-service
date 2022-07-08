package guru.sfg.beer.order.service.web.mappers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.services.beer.BeerService;
import guru.sfg.beer.order.service.web.model.BeerDto;
import guru.sfg.beer.order.service.web.model.BeerOrderLineDto;

public abstract class BeerOrderLineMapperDecorator implements BeerOrderLineMapper {

	private BeerService beerService;
	private BeerOrderLineMapper mapper;
	
	@Autowired
	public void setBeerService(BeerService beerService) {
		this.beerService= beerService;
	}

	@Autowired
	@Qualifier("delegate")
	public void setBeerOrderLineMapper(BeerOrderLineMapper beerOrderLineMapper) {
		this.mapper= beerOrderLineMapper;
	}

	@Override
	public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
		BeerOrderLineDto dto = mapper.beerOrderLineToDto(line);
		
		Optional<BeerDto> beerDtoOpt = beerService.getBeerByUpc(dto.getUpc());
		
		beerDtoOpt.ifPresent(beerDto -> {
			dto.setBeerName(beerDto.getBeerName());
			dto.setBeerStyle(beerDto.getBeerStyle().name());
			dto.setPrice(beerDto.getPrice());
			dto.setBeerId(beerDto.getId());
		});
		return dto;
	}
}

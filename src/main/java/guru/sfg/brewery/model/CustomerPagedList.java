package guru.sfg.brewery.model;

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class CustomerPagedList extends PageImpl<CustomerDto> {

	public CustomerPagedList(List<CustomerDto> content) {
		super(content);
	}

	public CustomerPagedList(List<CustomerDto> content, Pageable pageable, long total) {
		super(content, pageable, total);
	}

}

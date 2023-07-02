package fr.leblanc.webflux.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Table("car")
public class CarEntity {

	@Id
	private Integer id;
	
	private String brand;
	
	private Integer kilowatt;
	
}

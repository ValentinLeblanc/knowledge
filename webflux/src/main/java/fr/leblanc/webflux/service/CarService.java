package fr.leblanc.webflux.service;

import org.springframework.stereotype.Service;

import fr.leblanc.webflux.controller.dto.CarDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface CarService {

	Mono<CarDto> getCar(Integer id);
	
	Mono<CarDto> createCar(CarDto carDto);
	
	Mono<CarDto> updateCar(Integer carId, CarDto carDto);
	
	Mono<Void> deleteCar(Integer carId);

	Flux<CarDto> getAllCars();
		
}

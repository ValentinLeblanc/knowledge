package fr.leblanc.webflux.controller.dto;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.leblanc.webflux.service.CarService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/car")
@RequiredArgsConstructor
public class CarController {
	
	private final CarService carService;
	
	@GetMapping("/{carId}")
	public Mono<CarDto> getCar(@PathVariable Integer carId) {
		
		Mono<CarDto> carMono = carService.getCar(carId);
		
		return carMono;
	}
	
	@PostMapping
	public Mono<CarDto> createCar(@RequestBody CarDto carDto) {
		return carService.createCar(carDto);
	}
	
	 @PutMapping("/{carId}")
	 public Mono<CarDto> updateCar(@PathVariable Integer carId, @RequestBody CarDto carDto) {
		 return carService.updateCar(carId, carDto);
	 }
	 
	 @DeleteMapping("/{carId}")
	 public Mono<Void> deleteCar(@PathVariable Integer carId, @RequestBody CarDto carDto) {
		 return carService.deleteCar(carId);
	 }
	 
	 @GetMapping("/all")
	 public Flux<CarDto> getAllCars() {
		 return carService.getAllCars();
	 }
	
}

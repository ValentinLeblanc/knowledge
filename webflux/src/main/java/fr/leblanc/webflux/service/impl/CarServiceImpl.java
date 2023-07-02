package fr.leblanc.webflux.service.impl;

import org.springframework.stereotype.Service;

import fr.leblanc.webflux.controller.dto.CarDto;
import fr.leblanc.webflux.repository.CarRepository;
import fr.leblanc.webflux.repository.entity.CarEntity;
import fr.leblanc.webflux.service.CarService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {

	private final CarRepository carRepository;
	
	@Override
	public Mono<CarDto> getCar(Integer carId) {
		return carRepository.findById(carId)
				.map(carEntity -> new CarDto(carEntity.getId(), Thread.currentThread().getName(), carEntity.getKilowatt()));
	}

	@Override
	public Mono<CarDto> createCar(CarDto carDto) {
		return carRepository.save(CarEntity.builder()
				.brand(carDto.brand())
				.kilowatt(carDto.kilowatt())
				.build())
				.map(carEntity -> new CarDto(carEntity.getId(), carEntity.getBrand(), carEntity.getKilowatt()));
	}

	@Override
	public Mono<CarDto> updateCar(Integer carId, CarDto carDto) {
		return carRepository.save(CarEntity.builder()
				.id(carId)
				.brand(carDto.brand())
				.kilowatt(carDto.kilowatt())
				.build())
				.map(carEntity -> new CarDto(carEntity.getId(), carEntity.getBrand(), carEntity.getKilowatt()));
	}

	@Override
	public Mono<Void> deleteCar(Integer carId) {
		return carRepository.deleteById(carId);
	}

	@Override
	public Flux<CarDto> getAllCars() {
		return carRepository.findAll().map(carEntity -> {
			return new CarDto(carEntity.getId(), Thread.currentThread().getName(), carEntity.getKilowatt());
		}).log();

	}

}

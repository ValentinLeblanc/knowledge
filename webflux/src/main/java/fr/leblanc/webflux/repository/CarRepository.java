package fr.leblanc.webflux.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import fr.leblanc.webflux.repository.entity.CarEntity;

public interface CarRepository extends R2dbcRepository<CarEntity, Integer> {

}

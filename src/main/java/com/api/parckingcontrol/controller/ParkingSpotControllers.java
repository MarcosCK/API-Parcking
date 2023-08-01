package com.api.parckingcontrol.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.parckingcontrol.dtos.ParkingSpotDtos;
import com.api.parckingcontrol.models.ParkingSpotModel;
import com.api.parckingcontrol.services.ParkingSpotService;

import jakarta.validation.Valid;

@RestController    
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/parking-spot")
public class ParkingSpotControllers {

	final ParkingSpotService parkingSpotService;

	public ParkingSpotControllers(ParkingSpotService parkingSpotService) {
		this.parkingSpotService = parkingSpotService;
	}
	
	@PostMapping(value = "/", consumes = {"*/*"})
	public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDtos parkingSpotDtos) {
		
		 if(parkingSpotService.existsByLicensePlateCar(parkingSpotDtos.getLicensePlateCar())){
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
	        }
	        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDtos.getParkingSpotNumber())){
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
	        }
	        if(parkingSpotService.existsByApartmentAndBlock(parkingSpotDtos.getApartment(), parkingSpotDtos.getBlock())){
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block!");
	        }
		
		var parkingSpotModel = new ParkingSpotModel();
		BeanUtils.copyProperties(parkingSpotDtos, parkingSpotModel);
		parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
		return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
	}

}

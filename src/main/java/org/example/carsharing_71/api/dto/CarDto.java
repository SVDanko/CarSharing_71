package org.example.carsharing_71.api.dto;

import org.example.carsharing_71.domain.Car;

/**
 * DTO автомобиля для REST‑ответов.
 * Содержит сведения о модели, характеристиках и локации.
 */

public class CarDto {
    private Long id;
    private String brand;
    private String model;
    private int seats;
    private String transmission;
    private String fuelType;
    private String plateNumber;
    private Long locationId;
    private String locationName;
    private String status;

    /**
     * Маппит сущность Car в DTO.
     */
    public static CarDto fromEntity(Car car) {
        CarDto dto = new CarDto();
        dto.setId(car.getId());
        dto.setBrand(car.getCarModel().getBrand());
        dto.setModel(car.getCarModel().getModelName());
        dto.setSeats(car.getCarModel().getSeats());
        dto.setTransmission(car.getCarModel().getTransmission().name());
        dto.setFuelType(car.getCarModel().getFuelType().name());
        dto.setPlateNumber(car.getPlateNumber());
        dto.setLocationId(car.getLocation().getId());
        dto.setLocationName(car.getLocation().getName());
        dto.setStatus(car.getStatus().name());
        return dto;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public int getSeats() {
        return seats;
    }
    public void setSeats(int seats) {
        this.seats = seats;
    }
    public String getTransmission() {
        return transmission;
    }
    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }
    public String getFuelType() {
        return fuelType;
    }
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
    public String getPlateNumber() {
        return plateNumber;
    }
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
    public Long getLocationId() {
        return locationId;
    }
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
    public String getLocationName() {
        return locationName;
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}

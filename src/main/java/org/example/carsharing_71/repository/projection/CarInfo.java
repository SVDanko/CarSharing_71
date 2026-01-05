package org.example.carsharing_71.repository.projection;

public interface CarInfo {
    Long getId();
    String getPlateNumber();
    ModelInfo getModel();

    interface ModelInfo {
        String getBrand();
        String getModelName();
    }
}

package org.example.carsharing_71.repository.projection;

import org.springframework.beans.factory.annotation.Value;

public interface CarInfo {
    Long getId();
    String getPlateNumber();

    @Value("#{target.carModel}")
    ModelInfo getModel();

    interface ModelInfo {
        String getBrand();
        String getModelName();
    }
}

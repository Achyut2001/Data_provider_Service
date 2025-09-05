package com.tdit.dataprovideservice.service;

import com.tdit.dataprovideservice.entity.Property;
import com.tdit.dataprovideservice.entity.Status;
import com.tdit.dataprovideservice.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyServiceAdmin {

    private final PropertyRepository propertyRepository;

    public void updatePropertyStatus(Long id, String status) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setStatus(status);
        propertyRepository.save(property);
    }
    public List<Property> getRejectedProperties() {
        return propertyRepository.findByStatus(Status.REJECTED.name());
    }
}

package com.example.SpringDemo.service;

import com.example.SpringDemo.entity.Specialization;
import com.example.SpringDemo.repository.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecializationService {
    
    @Autowired
    private SpecializationRepository specializationRepository;
    
    public Specialization createSpecialization(Specialization specialization) {
        if (specializationRepository.existsByName(specialization.getName())) {
            throw new RuntimeException("Specialization with this name already exists");
        }
        return specializationRepository.save(specialization);
    }
    
    public Page<Specialization> getAllSpecializations(Pageable pageable) {
        return specializationRepository.findAll(pageable);
    }
    
    public List<Specialization> getActiveSpecializations() {
        return specializationRepository.findAllActive();
    }
    
    public Page<Specialization> searchSpecializations(String name, Specialization.Status status, Pageable pageable) {
        return specializationRepository.findSpecializationsWithFilters(name, status, pageable);
    }
    
    public Optional<Specialization> getSpecializationById(Long id) {
        return specializationRepository.findById(id);
    }
    
    public Specialization updateSpecialization(Long id, Specialization specialization) {
        Specialization existingSpecialization = specializationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialization not found"));
        
        if (!existingSpecialization.getName().equals(specialization.getName()) && 
            specializationRepository.existsByName(specialization.getName())) {
            throw new RuntimeException("Specialization with this name already exists");
        }
        
        existingSpecialization.setName(specialization.getName());
        existingSpecialization.setDescription(specialization.getDescription());
        existingSpecialization.setStatus(specialization.getStatus());
        
        return specializationRepository.save(existingSpecialization);
    }
    
    public void deleteSpecialization(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialization not found"));
        
        specializationRepository.delete(specialization);
    }
}

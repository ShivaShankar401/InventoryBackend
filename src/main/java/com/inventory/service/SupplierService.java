package com.inventory.service;

import com.inventory.model.Supplier;
import com.inventory.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> findAll() { return supplierRepository.findAll(); }

    public Optional<Supplier> findById(Long id) { return supplierRepository.findById(id); }

    public List<Supplier> search(String q) { return supplierRepository.search(q); }

    public Supplier save(Supplier s) { return supplierRepository.save(s); }

    public void delete(Long id) { supplierRepository.deleteById(id); }
}

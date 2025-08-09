package com.example.springboot.repositories;

import com.example.springboot.models.VendedorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VendedorRepository extends JpaRepository<VendedorModel, UUID> { //
    // Interface que estende JpaRepository para operações CRUD em VendedorModel
    // O UUID é o tipo de dado usado como identificador único para os vendedores
}
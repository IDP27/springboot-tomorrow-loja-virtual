package com.example.springboot.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRecordDto( // Define um DTO (Data Transfer Object) para produtos

        @NotBlank(message = "O nome é obrigatório") // Valida que o nome não pode ser vazio
        String name,

        @NotNull(message = "O valor é obrigatório") // Valida que o valor não pode ser nulo
        BigDecimal value,

        @NotNull(message = "O ID do vendedor é obrigatório") // Valida que o ID do vendedor não pode ser nulo
        UUID vendedorId

) {}
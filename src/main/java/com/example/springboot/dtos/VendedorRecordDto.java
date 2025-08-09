package com.example.springboot.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VendedorRecordDto(

        @NotBlank(message = "O nome é obrigatório") // Valida que o nome não pode ser vazio
        String nome,

        @NotBlank(message = "O e-mail é obrigatório") // Valida que o e-mail não pode ser vazio
        @Email(message = "Formato de e-mail inválido") // Valida que o e-mail não pode ser vazio e tem formato válido
        String email

) {}
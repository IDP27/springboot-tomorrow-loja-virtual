package com.example.springboot.controllers;

import com.example.springboot.dtos.VendedorRecordDto; // DTO usado para receber dados da requisição
import com.example.springboot.models.VendedorModel;   // Entidade do vendedor
import com.example.springboot.repositories.VendedorRepository; // Interface para operações no banco

import jakarta.validation.Valid; // Anotação para validar os dados recebidos

import org.springframework.beans.BeanUtils; // Utilitário para copiar propriedades de objetos
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel; // Representa coleção com links HATEOAS
import org.springframework.hateoas.EntityModel;   // Representa recurso único com links HATEOAS
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Métodos para criar links HATEOAS

@RestController
@RequestMapping("/vendedores") // Define o endpoint base
public class VendedorController {

    @Autowired
    private VendedorRepository vendedorRepository; // Injeção de dependência do repositório

    // -------------------- POST: Criar novo vendedor --------------------
    @PostMapping
    public ResponseEntity<VendedorModel> saveVendedor(@RequestBody @Valid VendedorRecordDto vendedorRecordDto) {
        // Cria nova instância da entidade
        VendedorModel vendedorModel = new VendedorModel();
        // Copia dados do DTO para a entidade
        BeanUtils.copyProperties(vendedorRecordDto, vendedorModel);
        // Retorna status 201 com o objeto salvo no banco
        return ResponseEntity.status(HttpStatus.CREATED).body(vendedorRepository.save(vendedorModel));
    }

    // -------------------- GET: Buscar todos os vendedores (com HATEOAS) --------------------
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<VendedorModel>>> getAllVendedores() {
        // Busca todos no banco
        List<VendedorModel> vendedores = vendedorRepository.findAll();

        // Adiciona links HATEOAS a cada vendedor
        List<EntityModel<VendedorModel>> vendedoresComLinks = vendedores.stream().map(vendedor ->
                EntityModel.of(vendedor,
                        // Link para ele mesmo
                        linkTo(methodOn(VendedorController.class).getOneVendedor(vendedor.getIdVendedor())).withSelfRel(),
                        // Link para a lista de todos
                        linkTo(methodOn(VendedorController.class).getAllVendedores()).withRel("vendedores")
                )
        ).collect(Collectors.toList());

        // Retorna coleção com link para si mesma
        return ResponseEntity.ok(
                CollectionModel.of(vendedoresComLinks,
                        linkTo(methodOn(VendedorController.class).getAllVendedores()).withSelfRel())
        );
    }

    // -------------------- GET: Buscar vendedor específico (com HATEOAS) --------------------
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<VendedorModel>> getOneVendedor(@PathVariable UUID id) {
        // Busca por ID
        Optional<VendedorModel> vendedorOptional = vendedorRepository.findById(id);
        if (vendedorOptional.isEmpty()) {
            // Retorna 404 se não encontrar
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        VendedorModel vendedor = vendedorOptional.get();
        // Adiciona links para o vendedor atual e para a lista geral
        EntityModel<VendedorModel> vendedorModel = EntityModel.of(vendedor,
                linkTo(methodOn(VendedorController.class).getOneVendedor(id)).withSelfRel(),
                linkTo(methodOn(VendedorController.class).getAllVendedores()).withRel("vendedores")
        );

        return ResponseEntity.ok(vendedorModel);
    }

    // -------------------- PUT: Atualizar vendedor --------------------
    @PutMapping("/{id}")
    public ResponseEntity<VendedorModel> updateVendedor(@PathVariable UUID id, @RequestBody @Valid VendedorRecordDto vendedorRecordDto) {
        // Busca vendedor existente
        Optional<VendedorModel> vendedorOptional = vendedorRepository.findById(id);
        if (vendedorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Copia novas propriedades mantendo o ID
        VendedorModel vendedorModel = vendedorOptional.get();
        BeanUtils.copyProperties(vendedorRecordDto, vendedorModel);
        vendedorModel.setIdVendedor(id);

        return ResponseEntity.ok(vendedorRepository.save(vendedorModel));
    }

    // -------------------- DELETE: Deletar vendedor --------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendedor(@PathVariable UUID id) {
        Optional<VendedorModel> vendedorOptional = vendedorRepository.findById(id);
        if (vendedorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        vendedorRepository.delete(vendedorOptional.get());
        return ResponseEntity.noContent().build();
    }
}

/*
	1.	Finalidade da classe: Controlar todas as operações CRUD de vendedores, implementando boas práticas REST e HATEOAS.
	2.	Endpoints implementados:
	•	POST /vendedores → Criação de vendedor.
	•	GET /vendedores → Lista todos os vendedores com links.
	•	GET /vendedores/{id} → Retorna um vendedor específico com links.
	•	PUT /vendedores/{id} → Atualiza dados de um vendedor.
	•	DELETE /vendedores/{id} → Remove um vendedor.
	3.	HATEOAS:
	•	Links de navegação para facilitar consumo por APIs REST.
	•	Em cada recurso, há link para si mesmo (self) e para a lista de vendedores.
	4.	Validação:
	•	Usa @Valid para garantir integridade dos dados de entrada.
	5.	Injeção de dependência:
	•	@Autowired para acessar VendedorRepository sem precisar instanciar manualmente.
	6.	BeanUtils:
	•	Facilita a cópia de dados do DTO para a entidade, evitando código repetitivo.
 */
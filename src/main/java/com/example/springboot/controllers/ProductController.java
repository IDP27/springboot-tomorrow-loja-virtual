package com.example.springboot.controllers;

// Importações necessárias
import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.models.VendedorModel;
import com.example.springboot.repositories.ProductRepository;
import com.example.springboot.repositories.VendedorRepository;

import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel; // Representa um recurso com links HATEOAS
import org.springframework.hateoas.CollectionModel; // Representa coleção de recursos com links HATEOAS
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
// Importa métodos utilitários para criar links no HATEOAS

@RestController
@RequestMapping("/products") // Define que todos os endpoints começarão com /products
public class ProductController {

    @Autowired
    private ProductRepository productRepository; // Acesso ao banco de dados de produtos

    @Autowired
    private VendedorRepository vendedorRepository; // Acesso ao banco de dados de vendedores

    // ======================= CREATE =======================
    // POST - Criar produto
    @PostMapping
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {

        // Busca o vendedor pelo ID informado no DTO
        Optional<VendedorModel> vendedorOptional = vendedorRepository.findById(productRecordDto.vendedorId());
        if (vendedorOptional.isEmpty()) {
            // Caso não encontre o vendedor, retorna 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Cria o objeto ProductModel e copia os dados do DTO
        ProductModel productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);

        // Associa o vendedor ao produto
        productModel.setVendedor(vendedorOptional.get());

        // Salva o produto no banco e retorna status 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    // ======================= READ ALL =======================
    // GET - Listar todos os produtos com HATEOAS
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProductModel>>> getAllProducts() {

        // Busca todos os produtos no banco
        List<ProductModel> products = productRepository.findAll();

        // Converte cada produto em um EntityModel com links HATEOAS
        List<EntityModel<ProductModel>> productsWithLinks = products.stream().map(product ->
                EntityModel.of(product,
                        linkTo(methodOn(ProductController.class).getOneProduct(product.getIdProduct())).withSelfRel(), // Link para o próprio recurso
                        linkTo(methodOn(ProductController.class).getAllProducts()).withRel("products") // Link para lista de produtos
                )
        ).collect(Collectors.toList());

        // Retorna a coleção de produtos com um link para si mesma
        return ResponseEntity.ok(
                CollectionModel.of(productsWithLinks,
                        linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel())
        );
    }

    // ======================= READ ONE =======================
    // GET - Obter um único produto com HATEOAS
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductModel>> getOneProduct(@PathVariable UUID id) {

        // Busca o produto pelo ID
        Optional<ProductModel> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ProductModel product = productOptional.get();

        // Cria o EntityModel com os links HATEOAS
        EntityModel<ProductModel> productModel = EntityModel.of(product,
                linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel(),
                linkTo(methodOn(ProductController.class).getAllProducts()).withRel("products")
        );

        return ResponseEntity.ok(productModel);
    }

    // ======================= UPDATE =======================
    // PUT - Atualizar produto
    @PutMapping("/{id}")
    public ResponseEntity<ProductModel> updateProduct(@PathVariable UUID id, @RequestBody @Valid ProductRecordDto productRecordDto) {

        // Busca o produto no banco
        Optional<ProductModel> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Busca o vendedor associado
        Optional<VendedorModel> vendedorOptional = vendedorRepository.findById(productRecordDto.vendedorId());
        if (vendedorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Atualiza os dados do produto
        ProductModel productModel = productOptional.get();
        BeanUtils.copyProperties(productRecordDto, productModel);
        productModel.setIdProduct(id);
        productModel.setVendedor(vendedorOptional.get());

        return ResponseEntity.ok(productRepository.save(productModel));
    }

    // ======================= DELETE =======================
    // DELETE - Remover produto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {

        Optional<ProductModel> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        productRepository.delete(productOptional.get());
        return ResponseEntity.noContent().build();
    }
}
/*
	•	DTO (ProductRecordDto): transporta os dados da requisição e agora inclui o vendedorId.
	•	Validação: uso de @Valid para garantir que os dados recebidos sejam válidos.
	•	BeanUtils.copyProperties: facilita a cópia dos atributos do DTO para o Model.
	•	Optional: evita NullPointerException e permite verificar se o recurso existe.
	•	HATEOAS: adicionado para fornecer links navegáveis na resposta, seguindo princípios de RESTful APIs.
	•	ResponseEntity: usado para controlar o status HTTP e o corpo da resposta.
 */
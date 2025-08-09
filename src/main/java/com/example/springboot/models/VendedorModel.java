package com.example.springboot.models;
// Define o pacote onde a classe está localizada, neste caso no pacote models dentro do projeto Spring Boot.

import com.fasterxml.jackson.annotation.JsonIgnore;
// Importa a anotação JsonIgnore, usada para evitar que certos campos sejam serializados no JSON.

import jakarta.persistence.*;
// Importa as anotações necessárias para mapear a classe como uma entidade JPA.

import java.io.Serializable;
// Importa a interface Serializable, que permite que objetos dessa classe sejam transformados em bytes (necessário para entidades JPA).

import java.util.List;
// Importa a interface List, usada para armazenar uma coleção de produtos associados ao vendedor.

import java.util.UUID;
// Importa a classe UUID, utilizada como tipo para o identificador único do vendedor.

@Entity
// Indica que esta classe é uma entidade JPA e será mapeada para uma tabela no banco de dados.

@Table(name = "TB_VENDEDORES")
// Especifica o nome da tabela no banco de dados que armazenará os vendedores.

public class VendedorModel implements Serializable {
// Declara a classe VendedorModel, que implementa Serializable para permitir persistência e transporte do objeto.

    private static final long serialVersionUID = 1L;
    // Identificador de versão para serialização, usado para garantir compatibilidade durante a leitura de objetos.

    @Id
    // Indica que o campo abaixo é a chave primária da tabela.

    @GeneratedValue(strategy=GenerationType.AUTO)
    // Define que o valor do ID será gerado automaticamente pelo banco de dados, utilizando UUID.

    private UUID idVendedor;
    // Campo que armazena o identificador único do vendedor.

    private String nome;
    // Campo que armazena o nome do vendedor.

    private String email;
    // Campo que armazena o email do vendedor.

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL)
    // Define um relacionamento "um para muitos" com a entidade ProductModel.
    // O atributo "mappedBy" indica que o mapeamento é feito pela propriedade "vendedor" na classe ProductModel.
    // CascadeType.ALL significa que todas as operações (persistir, atualizar, excluir) feitas no vendedor serão aplicadas também aos produtos.

    @JsonIgnore
    // Impede que a lista de produtos seja serializada para JSON, evitando problemas de recursão infinita.

    private List<ProductModel> produtos;
    // Lista que armazena todos os produtos associados a este vendedor.

    // ---------- MÉTODOS GETTERS E SETTERS ----------

    public UUID getIdVendedor() {
        return idVendedor;
    }
    // Retorna o valor do id do vendedor.

    public void setIdVendedor(UUID idVendedor) {
        this.idVendedor = idVendedor;
    }
    // Define o valor do id do vendedor.

    public String getNome() {
        return nome;
    }
    // Retorna o nome do vendedor.

    public void setNome(String nome) {
        this.nome = nome;
    }
    // Define o nome do vendedor.

    public String getEmail() {
        return email;
    }
    // Retorna o email do vendedor.

    public void setEmail(String email) {
        this.email = email;
    }
    // Define o email do vendedor.

    public List<ProductModel> getProdutos() {
        return produtos;
    }
    // Retorna a lista de produtos associados ao vendedor.

    public void setProdutos(List<ProductModel> produtos) {
        this.produtos = produtos;
    }
    // Define a lista de produtos associados ao vendedor.
}
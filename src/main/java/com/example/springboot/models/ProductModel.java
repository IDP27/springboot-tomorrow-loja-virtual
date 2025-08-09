// Pacote onde a classe está organizada no projeto
package com.example.springboot.models;

// Importa a classe VendedorModel (relacionamento entre entidades)
import com.example.springboot.models.VendedorModel;

// Importa anotações e tipos do JPA para mapeamento ORM
import jakarta.persistence.*;

// Importa RepresentationModel do Spring HATEOAS para permitir adicionar links ao recurso
import org.springframework.hateoas.RepresentationModel;

// Importa interfaces para serialização e tipos utilitários
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

// Anotação que indica que esta classe é uma entidade JPA (será mapeada para uma tabela)
@Entity
// Define o nome da tabela no banco de dados
@Table(name = "TB_PRODUCTS")
// Declaração da classe: estende RepresentationModel para suportar HATEOAS e implementa Serializable
public class ProductModel extends RepresentationModel<ProductModel> implements Serializable {
    // Versão para controle de compatibilidade da serialização
    private static final long serialVersionUID = 1L;

    // Indica que este campo é a chave primária da entidade
    @Id
    // Define a estratégia de geração automática do ID (UUID será gerado pelo provedor)
    @GeneratedValue(strategy= GenerationType.AUTO)
    // Campo que representa o identificador do produto (UUID)
    private UUID idProduct;

    // Campo para o nome do produto
    private String name;

    // Campo para o valor do produto; BigDecimal é usado para precisão com valores monetários
    @Column(name = "product_value")
    private BigDecimal value;

    // Define relação Many-to-One: muitos produtos podem pertencer a um vendedor
    @ManyToOne
    // Especifica a coluna de junção no banco que referencia o vendedor
    @JoinColumn(name = "vendedor_id")
    // Campo que referencia o vendedor associado a este produto
    private VendedorModel vendedor;

    // -------------------- GETTERS / SETTERS --------------------

    // Retorna o ID do produto
    public UUID getIdProduct() {
        return idProduct;
    }

    // Define o ID do produto (usado, por exemplo, em atualizações ou testes)
    public void setIdProduct(UUID idProduct) {
        this.idProduct = idProduct;
    }

    // Retorna o nome do produto
    public String getName() {
        return name;
    }

    // Define o nome do produto
    public void setName(String name) {
        this.name = name;
    }

    // Retorna o valor do produto
    public BigDecimal getValue() {
        return value;
    }

    // Define o valor do produto
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    // Retorna o vendedor associado ao produto
    public VendedorModel getVendedor() {
        return vendedor;
    }

    // Associa um vendedor ao produto
    public void setVendedor(VendedorModel vendedor) {
        this.vendedor = vendedor;
    }
}
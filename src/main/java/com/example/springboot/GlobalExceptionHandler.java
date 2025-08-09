package com.example.springboot;

// Importa o controller apenas se precisar fazer referência direta (aqui não é necessário)
import com.example.springboot.controllers.ProductController;

// Importações de classes para trabalhar com respostas HTTP e códigos de status
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// Importação para capturar erros de validação do @Valid
import org.springframework.web.bind.MethodArgumentNotValidException;

// Importação para tratar exceções de forma global
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Para obter detalhes da requisição, como URL e parâmetros
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime; // Para registrar o momento do erro
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// Marca esta classe como um "tratador global" de exceções para todos os controllers
@ControllerAdvice
public class GlobalExceptionHandler {

    // ---------------------------
    // TRATAMENTO DE ERROS DE VALIDAÇÃO
    // ---------------------------

    // Indica que este método tratará especificamente erros do tipo MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {

        // Cria um mapa para armazenar informações do erro
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now()); // Data e hora em que o erro ocorreu
        body.put("status", HttpStatus.BAD_REQUEST.value()); // Código HTTP 400 (Bad Request)

        // Extrai todos os erros de validação campo a campo
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors() // Obtém a lista de erros nos campos
                .stream() // Inicia um fluxo de dados (stream)
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),          // Chave = nome do campo
                        fieldError -> fieldError.getDefaultMessage(), // Valor = mensagem de erro
                        (msg1, msg2) -> msg1                          // Se houver chave repetida, mantém o primeiro valor
                ));

        body.put("errors", errors); // Adiciona o mapa de erros no corpo da resposta
        body.put("message", "Erro de validação nos campos."); // Mensagem geral sobre o erro
        body.put("path", request.getDescription(false).replace("uri=", "")); // Caminho da requisição

        // Retorna a resposta com status 400 e o corpo contendo os detalhes do erro
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ---------------------------
    // TRATAMENTO GENÉRICO DE EXCEÇÕES
    // ---------------------------

    // Indica que este método tratará qualquer exceção não capturada por outros métodos
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {

        // Cria um mapa para armazenar informações do erro
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now()); // Data e hora do erro
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // Código HTTP 500 (Internal Server Error)
        body.put("message", ex.getMessage()); // Mensagem da exceção
        body.put("path", request.getDescription(false).replace("uri=", "")); // Caminho da requisição

        // Retorna a resposta com status 500 e detalhes do erro
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
package com.dio.padroesprojetospring.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import com.dio.padroesprojetospring.model.Endereco;

import com.dio.padroesprojetospring.model.Cliente;
import com.dio.padroesprojetospring.model.EnderecoRepository;
import com.dio.padroesprojetospring.model.ClienteRepository;
import com.dio.padroesprojetospring.service.ClienteService;
import com.dio.padroesprojetospring.service.ViaCepService;


public class ClienteServiceImpl implements ClienteService {
    
    // Singleton: Injetar os componentes do Spring com @Autowired.
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	
	// Strategy: Implementar os métodos definidos na interface.
	// Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

	// Buscar todos os Clientes.
	@Override
	public Iterable<Cliente> buscarTodos() {		
		return clienteRepository.findAll();
	}

	// Buscar Cliente por ID.
	@Override
	public Cliente buscarPorId(Long id) {		
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Cliente cliente) {
		salvarClienteComCep(cliente);
	}

	// Buscar Cliente por ID, caso exista:
	@Override	
	public void atualizar(Long id, Cliente cliente) {		
		Optional<Cliente> clienteBd = clienteRepository.findById(id);
		if (clienteBd.isPresent()) {
			salvarClienteComCep(cliente);
		}
	}

	// Deletar Cliente por ID.
	@Override
	public void deletar(Long id) {		
		clienteRepository.deleteById(id);
	}

	private void salvarClienteComCep(Cliente cliente) {
		// Verificar se o Endereco do Cliente já existe (pelo CEP).
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e persistir o retorno.
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		clienteRepository.save(cliente);
	}

}

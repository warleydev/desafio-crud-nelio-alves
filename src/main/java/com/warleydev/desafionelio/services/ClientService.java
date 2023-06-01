package com.warleydev.desafionelio.services;

import com.warleydev.desafionelio.entities.Client;
import com.warleydev.desafionelio.repositories.ClientRepository;
import com.warleydev.desafionelio.services.exceptions.InvalidCpfException;
import com.warleydev.desafionelio.services.exceptions.NullOrEmptyFieldException;
import com.warleydev.desafionelio.services.exceptions.ResourceNotFoundException;
import com.warleydev.desafionelio.utils.IsCPF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository repository;

    @Transactional(readOnly = true)
    public Page<Client> findAll(PageRequest pageRequest){
        return repository.findAll(pageRequest);
    }

    @Transactional(readOnly = true)
    public Client findById(Long id){
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado!"));
    }

    public Client insert(Client client){
        if (clientValidate(client)){
            return repository.save(client);
        }
        return null;
    }

    public Client update(Long id, Client updatedClient){
        if (repository.existsById(id)){
            updatedClient.setId(id);
            updatedClient = repository.save(updatedClient);
            return updatedClient;
        }
        else throw new ResourceNotFoundException("Id "+id+" não encontrado");
    }

    public void deleteById(Long id){
        if (repository.existsById(id)){
            repository.deleteById(id);
        }
        else throw new ResourceNotFoundException("Id "+id+" não encontrado!");
    }

    public boolean clientValidate(Client client){
        if (client.getName() == null || client.getName() == "" || client.getBirthDate() == null ||
        client.getChildren() == null || client.getIncome() == null || client.getCpf() == null){
            throw new NullOrEmptyFieldException("Preencha todos os campos!");
        }

        if (IsCPF.isValidcpf(client.getCpf())){
            if (cpfAlreadyRegistered(client.getCpf())){
                throw new InvalidCpfException("CPF '"+client.getCpf()+"' já existe.");
            }
            else{
                return true;
            }
        }
        else {
            throw new InvalidCpfException("CPF '"+client.getCpf()+"' inválido!");
        }
    }

    public boolean cpfAlreadyRegistered(String cpf){
        return repository.existsByCpf(cpf);
    }
}

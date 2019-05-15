package com.vvorobel.bonds4all.Controller;

import com.vvorobel.bonds4all.model.Client;
import com.vvorobel.bonds4all.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ClientsController {
    @Autowired
    private ClientRepository clientRepository;


    @GetMapping("/clients")
    public Iterable<Client> getClients() {
        return clientRepository.findAll();
    }

    @PostMapping("/clients")
    public Client createClient(@Valid @RequestBody Client client) {
        return clientRepository
                .findByEmail(client.getEmail())
                .orElse(clientRepository.save(client));
    }
}

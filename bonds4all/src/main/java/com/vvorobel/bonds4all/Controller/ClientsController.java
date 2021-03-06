package com.vvorobel.bonds4all.Controller;

import com.vvorobel.bonds4all.model.Client;
import com.vvorobel.bonds4all.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {
        Client oldClient = clientRepository.findByEmail(client.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(oldClient !=null ? oldClient : clientRepository.save(client));
    }
}

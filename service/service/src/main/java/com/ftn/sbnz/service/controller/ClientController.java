package com.ftn.sbnz.service.controller;

import com.ftn.sbnz.model.facts.Client;
import com.ftn.sbnz.service.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClient(@PathVariable String clientId) {
        Client client = clientService.getClientById(clientId);
        return client != null
                ? ResponseEntity.ok(client)
                : ResponseEntity.notFound().build();
    }
}
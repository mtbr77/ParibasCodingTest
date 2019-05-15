package com.vvorobel.bonds4all.repository;

import com.vvorobel.bonds4all.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findByEmail(String email);
}

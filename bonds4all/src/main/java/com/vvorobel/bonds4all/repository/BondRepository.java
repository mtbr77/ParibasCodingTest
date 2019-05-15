package com.vvorobel.bonds4all.repository;

import com.vvorobel.bonds4all.model.Bond;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BondRepository extends JpaRepository<Bond, Integer> {
    List<Bond> findByClientId(int clientId);
}

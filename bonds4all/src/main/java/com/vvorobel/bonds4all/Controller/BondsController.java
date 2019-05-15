package com.vvorobel.bonds4all.Controller;

import com.vvorobel.bonds4all.model.Bond;
import com.vvorobel.bonds4all.repository.BondRepository;
import com.vvorobel.bonds4all.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class BondsController {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BondRepository bondRepository;

    @GetMapping("/clients/{id}/bonds")
    public List<Bond> getClientBonds(@PathVariable("id") int id) {
        return bondRepository.findByClientId(id);

    }

    @PostMapping("/clients/{id}/bonds")
    public Bond createBond(@Valid @RequestBody Bond bond, @PathVariable("id") int clientId, HttpServletRequest request) {
        bond.setClientId(clientId);
        bond.setTime(LocalDateTime.now());
        bond.setIp(request.getRemoteAddr());
        return bondRepository.save(bond);
    }
}

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
import java.util.stream.Collectors;

import static com.vvorobel.bonds4all.Controller.Regulations.*;

@RestController
public class BondsController {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BondRepository bondRepository;

    @GetMapping("/clients/{id}/bonds")
    public List<Bond> getClientBonds(@PathVariable("id") int id) {
        List<Bond> bonds = bondRepository.findByClientId(id);
        if (bonds.isEmpty()) throw new ResourceNotFoundException(id);
        return bonds;
    }

    @PostMapping("/clients/{id}/bonds")
    public Bond createBond(@Valid @RequestBody Bond bond, @PathVariable("id") int clientId, HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        if (bond.getAmount() > MAX_BOND_AMOUNT && (now.getHour() >= MIN_ILLEGAL_TIME || now.getHour() < MAX_ILLEGAL_TIME)) throw new ValidationException();
        String ip = request.getRemoteAddr();
        List<Bond> soldedBonds = bondRepository.findByIp(ip).stream()
                .filter(sBond -> sBond.getTime().toLocalDate().equals(now.toLocalDate()))
                .collect( Collectors.toList() );
        if (soldedBonds.size() > MAX_SOLDED_BONDS_PER_DAY) throw new ValidationException();
        bond.setClientId(clientId);
        bond.setTime(now);
        bond.setIp(ip);
        return bondRepository.save(bond);
    }
}

package com.vvorobel.bonds4all.Controller;

import com.vvorobel.bonds4all.model.Bond;
import com.vvorobel.bonds4all.repository.BondRepository;
import com.vvorobel.bonds4all.repository.ClientRepository;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/clients/{id}/bonds")
    public List<Bond> getClientBonds(@PathVariable("id") int id) {
        List<Bond> bonds = bondRepository.findByClientId(id);
        if (bonds.isEmpty()) throw new ResourceNotFoundException(id);
        return bonds;
    }

    @PostMapping("/clients/{id}/bonds")
    public ResponseEntity<Bond> createBond(@Valid @RequestBody Bond bond, @PathVariable("id") int clientId, HttpServletRequest request) {
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
        return ResponseEntity.status(HttpStatus.CREATED).body(bondRepository.save(bond));
    }

    @PutMapping("/clients/{id}/bonds/{bondId}")
    public Bond adjustBondTerm(@RequestBody Bond newBond, @PathVariable("id") int clientId, @PathVariable("bondId") int bondId, HttpServletRequest request) {
        List<Bond> bonds = bondRepository.findByClientId(clientId);
        if (bonds.isEmpty()) throw new ResourceNotFoundException(clientId);
        Bond oldBond = bonds.stream()
                .filter(sBond -> bondId == sBond.getId())
                .findAny().orElseThrow(() -> new ResourceNotFoundException(bondId));
        oldBond.setTime(LocalDateTime.now());
        oldBond.setIp(request.getRemoteAddr());
        int newTerm = newBond.getTerm();
        int oldTerm = oldBond.getTerm();
        oldBond.setTerm(newTerm);
        if (newTerm > oldTerm)
            oldBond.setCoupon(oldBond.getCoupon() * (1 - COUPON_DECREASE_PERCENT/100.0));
        return bondRepository.save(oldBond);
    }


    @GetMapping("/clients/{id}/bonds/{bondId}/history")
    public List<Bond> adjustBondTerm(@PathVariable("id") int clientId, @PathVariable("bondId") int bondId) {
        List<Bond> bonds = bondRepository.findByClientId(clientId);
        if (bonds.isEmpty()) throw new ResourceNotFoundException(clientId);
        Bond bond = bonds.stream()
                .filter(sBond -> bondId == sBond.getId())
                .findAny().orElseThrow(() -> new ResourceNotFoundException(bondId));

        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<Object[]> result = reader.createQuery().forRevisionsOfEntity(Bond.class, false, false)
                .add(AuditEntity.id().eq(bondId))
                .getResultList();
        return result.stream()
                .filter(x -> x instanceof Object[])
                .map(x -> (Object[])x)
                .map(x -> (Bond)((Object[])x)[0])
                .collect(Collectors.toList());

    }
}

package com.vvorobel.bonds4all;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvorobel.bonds4all.model.Bond;
import com.vvorobel.bonds4all.model.Client;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ContextConfiguration
//@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Bonds4allIT {
    private final ObjectMapper om = new ObjectMapper();

    private String expectedClient1, expectedClient2, expectedBond1, expectedAdjustedBond1;
    private Client client1, client2;
    private  Bond bond1, adjustedBond1;

    @LocalServerPort
    private int port;

    private String base;

    @Autowired
    private TestRestTemplate restTemplate;

    /*@MockBean
    private ClientRepository clientRepository;*/

    @BeforeAll
    public void setUp() throws Exception {
        base = new URL("http://localhost:" + port).toString();

        client1 = new Client(1,"John", "Doe",  "jd@ya.com");
        client2 = new Client(2,"Roy", "Filding",  "rf@ya.com");

        expectedClient1 = om.writeValueAsString(client1);
        expectedClient2 = om.writeValueAsString(client2);

        bond1 = new Bond(6,5000);
        expectedBond1 = "{\"term\":6,\"amount\":5000}";

        om.findAndRegisterModules();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void test1_createClients() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(base + "/clients", client1, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expectedClient1, response.getBody(), false);
    }

    @Test
    public void test2_getClients() throws Exception {
        restTemplate.postForEntity(base + "/clients", client2, String.class);

        ResponseEntity<String> response = restTemplate.getForEntity(base + "/clients", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

        String expected = "[" + expectedClient1 + "," + expectedClient2 + "]";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void test3_createBond() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(base + "/clients/1/bonds", bond1, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

        bond1 = om.readValue(response.getBody(), Bond.class);
        JSONAssert.assertEquals(expectedBond1, response.getBody(), false);
    }

    @Test
    public void test4_getBonds() {
        ResponseEntity<String> response = restTemplate.getForEntity(base + "/clients/1/bonds", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
    }

    @Test
    public void test5_adjustBondTerm() throws Exception {
        adjustedBond1 = (Bond) bond1.clone();
        adjustedBond1.setTerm(7);
        expectedAdjustedBond1 = "{\"term\":7,\"amount\":5000,\"coupon\":4.5}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(new Bond(7)), headers);

        ResponseEntity<String> response = restTemplate.exchange(base + "/clients/1/bonds/" + adjustedBond1.getId(), HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expectedAdjustedBond1, response.getBody(), false);
    }

    @Test
    public void test6_getBondHistory() {
        ResponseEntity<String> response = restTemplate.getForEntity(base + "/clients/1/bonds/" +  bond1.getId() + "/history", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
    }

}




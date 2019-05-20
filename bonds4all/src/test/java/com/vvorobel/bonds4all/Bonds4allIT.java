package com.vvorobel.bonds4all;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvorobel.bonds4all.model.Client;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
//@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Bonds4allIT {
    private static final ObjectMapper om = new ObjectMapper();

    String expectedClient1, expectedClient2;
    Client client1, client2;

    @LocalServerPort
    private int port;

    private String base;

    @Autowired
    private TestRestTemplate restTemplate;

    /*@MockBean
    private ClientRepository clientRepository;*/

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port).toString();
        client1 = new Client(1,"John", "Doe",  "jd@ya.com");
        client2 = new Client(2,"Roy", "Filding",  "rf@ya.com");
        expectedClient1 = om.writeValueAsString(client1);//"{\"id\":1, \"name\":\"John\", \"surname\":\"Doe\", \"email\":\"jd@ya.com\"}";
        expectedClient2 = om.writeValueAsString(client2);
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
}




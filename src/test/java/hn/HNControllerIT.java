package hn;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class HNControllerIT {

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:8080/");
    }

    @Test
    public void getHome() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString(),
               String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }
}
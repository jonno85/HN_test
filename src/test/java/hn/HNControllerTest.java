package hn;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HNControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getResult() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/hn")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$", hasSize(10)));
    }

//    @Test
//    public void getResult2() throws Exception {
//        mvc.perform(MockMvcRequestBuilders.get("/hn-most-used-title-last-week")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }

//    @Test
//    public void getResult3() throws Exception {
//        mvc.perform(MockMvcRequestBuilders.get("/hn-most-used-title-for-user-big-karma")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
}
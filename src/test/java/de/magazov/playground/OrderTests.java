package de.magazov.playground;

import de.magazov.playground.InventoryDataRestApplication;
import de.magazov.playground.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest(classes = InventoryDataRestApplication.class)
public class OrderTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void deleteAllBeforeTests() throws Exception {
        orderRepository.deleteAll();
    }

    @Test
    public void shouldReturnRepositoryIndex() throws Exception {

        mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
                jsonPath("$._links.order").exists());
    }

    @Test
    public void shouldCreateEntity() throws Exception {

        mockMvc.perform(post("/order").content(
                "{\"id\": \"444\", \"products\": [{  \"name\": \"hhh\",\n" +
                        "      \"price\": \"99.99\"}, {  \"name\": \"hhh1\", \n" +
                        "\"price\": \"99.99\"}], \"buyerMail\": \"ewreg@dfgbd.de\"}")).andExpect(
                status().isCreated()).andExpect(
                header().string("Location", containsString("order/")));
    }

    @Test
    public void shouldThrowValidationExceptionOnCreateEntity() throws Exception {

        Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(post("/order").content(
                    "{\"id\": \"444\", \"buyerMail\": \"ewreg@dfgbd.de\"}")).andExpect(
                    status().isCreated()).andExpect(
                    header().string("Location", containsString("product/")));
        });
    }

    @Test
    public void shouldQueryEntity() throws Exception {

        mockMvc.perform(post("/order").content(
                "{\"id\": \"444\", \"products\": [{  \"name\": \"hhh\",\n" +
                        "      \"price\": \"99.99\"}, {  \"name\": \"hhh1\", \n" +
                        "\"price\": \"99.99\"}], \"buyerMail\": \"ewreg@dfgbd.de\"}")).andExpect(
                status().isCreated()).andExpect(
                header().string("Location", containsString("order/")));

        String from = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String to = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        mockMvc.perform(
                get("/order/search/findByTimeRange?from={from}&to={to}",
                        from, to)).andExpect(
                status().isOk()).andExpect(
                jsonPath("$._embedded.order[0].buyerMail").value(
                        "ewreg@dfgbd.de"));
    }
}

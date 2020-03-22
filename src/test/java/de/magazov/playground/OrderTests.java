package de.magazov.playground;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest(classes = InventoryDataRestApplication.class)
public class OrderTests {

    private static final String ORDER_ROOT = "/order";
    private static final String DATE_FORMAT  = "MM/dd/yyyy";
    private static final String LOCATION  = "Location";

    private static final String ID = "\"id\": \"444\"";
    private static final String BUYER_MAIL  = "\"buyerMail\": \"ewreg@dfgbd.de\"";
    private static final String PRICE  =  "\"price\": \"99.99\"";
    private static final String NAME1  = "\"name\": \"hhh\"";
    private static final String NAME2 = "\"name\": \"hhh1\"";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void deleteAllBeforeTests()  {
        orderRepository.deleteAll();
    }

    @Test
    public void shouldReturnRepositoryIndex() throws Exception {

        mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
                jsonPath("$._links.order").exists());
    }

    @Test
    public void shouldCreateEntity() throws Exception {

        mockMvc.perform(post(ORDER_ROOT).content(
                "{" + ID + ", \"products\": [{ " +  NAME1 + ",\n" +
                            PRICE + "}, { " + NAME2 + ", \n" +
                        PRICE + "}]," + BUYER_MAIL + "}")).andExpect(
                status().isCreated()).andExpect(
                header().string(LOCATION, containsString(ORDER_ROOT)));
    }

    @Test
    public void shouldThrowValidationExceptionOnCreateEntity() {

        Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(post(ORDER_ROOT).content(
                    "{" + ID + ", " + BUYER_MAIL + "}")).andExpect(
                    status().isCreated()).andExpect(
                    header().string(LOCATION, containsString(ORDER_ROOT)));
        });
    }

    @Test
    public void shouldQueryEntity() throws Exception {

        mockMvc.perform(post(ORDER_ROOT).content(
                "{" + ID + ", \"products\": [{" +  NAME1 + ",\n" +
                              PRICE +"}, { " + NAME2 + ", \n" +
                        PRICE + "}]," +  BUYER_MAIL + "}")).andExpect(
                status().isCreated()).andExpect(
                header().string(LOCATION, containsString(ORDER_ROOT)));

        String from = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        String to = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        mockMvc.perform(
                get("/order/search/findByTimeRange?from={from}&to={to}",
                        from, to)).andExpect(
                status().isOk()).andExpect(
                jsonPath("$._embedded.order[0].buyerMail").value(
                        "ewreg@dfgbd.de"));
    }
}

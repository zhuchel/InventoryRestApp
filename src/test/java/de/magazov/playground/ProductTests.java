package de.magazov.playground;


import de.magazov.playground.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.NestedServletException;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest(classes = InventoryDataRestApplication.class)
public class ProductTests {

	private static final String PRODUCT_ROOT = "/product";
	private static final String LOCATION  = "Location";

	private static final String SKU = "\"sku\": \"HREN\"";
	private static final String NAME = "\"name\": \"toilet paper\"";
	private static final String PRICE = "\"price\":\"10.99\"";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	public void deleteAllBeforeTests() {
		productRepository.deleteAll();
	}

	@Test
	public void shouldReturnRepositoryIndex() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
				jsonPath("$._links.products").exists());
	}

	@Test
	public void shouldCreateEntity() throws Exception {

		mockMvc.perform(post(PRODUCT_ROOT).content(
				"{" + SKU + ", " + NAME + ", " + PRICE + "}")).andExpect(
						status().isCreated()).andExpect(
								header().string(LOCATION, containsString(PRODUCT_ROOT)));
	}

	@Test
	public void shouldThrowValidationExceptionOnCreateEntity()  {

		Assertions.assertThrows(NestedServletException.class, () -> {
			mockMvc.perform(post(PRODUCT_ROOT).content(
					"{" + SKU+ "}")).andExpect(
					status().isCreated()).andExpect(
					header().string(LOCATION, containsString(PRODUCT_ROOT)));
		});
	}

	@Test
	public void shouldRetrieveEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post(PRODUCT_ROOT).content(
				"{" + SKU + ", " + NAME + "," +  PRICE + "}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader(LOCATION);
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.name").value("toilet paper")).andExpect(
						jsonPath("$.price").value("10.99"));
	}

	@Test
	public void shouldUpdateEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post(PRODUCT_ROOT).content(
				"{" + SKU + ", " + NAME + ",  \"price\":\"0.99\"}\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader(LOCATION);

		mockMvc.perform(put("/product/HREN").content(
				"{\"name\": \"fff\"," +  PRICE + "}")).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.name").value("fff")).andExpect(
						jsonPath("$.price").value("10.99"));
	}

	@Test
	public void shouldDeleteEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post(PRODUCT_ROOT).content(
				"{" + SKU + ", " + NAME + ", " + PRICE + "}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader(LOCATION);
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}
}

package de.magazov.playground;


import de.magazov.playground.InventoryDataRestApplication;
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

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	public void deleteAllBeforeTests() throws Exception {
		productRepository.deleteAll();
	}

	@Test
	public void shouldReturnRepositoryIndex() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
				jsonPath("$._links.products").exists());
	}

	@Test
	public void shouldCreateEntity() throws Exception {

		mockMvc.perform(post("/product").content(
				"{\"sku\": \"HREN\", \"name\": \"toilet paper\", \"price\":\"10.99\"}")).andExpect(
						status().isCreated()).andExpect(
								header().string("Location", containsString("product/")));
	}

	@Test
	public void shouldThrowValidationExceptionOnCreateEntity() throws Exception {

		Assertions.assertThrows(NestedServletException.class, () -> {
			mockMvc.perform(post("/product").content(
					"{\"sku\": \"HREN\"}")).andExpect(
					status().isCreated()).andExpect(
					header().string("Location", containsString("product/")));
		});
	}

	@Test
	public void shouldRetrieveEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/product").content(
				"{\"sku\": \"HREN\", \"name\": \"toilet paper\", \"price\":\"10.99\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.name").value("toilet paper")).andExpect(
						jsonPath("$.price").value("10.99"));
	}

	@Test
	public void shouldUpdateEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/product").content(
				"{\"sku\": \"HREN1\", \"name\": \"init\",  \"price\":\"0.99\"}\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(put("/product/HREN1").content(
				"{\"name\": \"fff\",  \"price\":\"10.99\"}")).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.name").value("fff")).andExpect(
						jsonPath("$.price").value("10.99"));
	}

	@Test
	public void shouldDeleteEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/product").content(
				"{ \"sku\": \"ggg\", \"name\":\"jjj\",  \"price\":\"10.99\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}
}

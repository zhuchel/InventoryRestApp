package de.magazov.playground.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Order implements Persistable<String> {

	@Id
	private String id;

	@NotEmpty
	private List<Product> products;
	@Email
	private String buyerMail;
	@CreatedDate
	private Date creationDate;
	private BigDecimal totalPrice = BigDecimal.ZERO;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.totalPrice =
				products.stream()
						.map(x -> x.getPrice())
						.reduce(BigDecimal.ZERO, BigDecimal::add);
		this.products = products;
	}

	public String getBuyerMail() {
		return buyerMail;
	}

	public void setBuyerMail(String buyerMail) {
		this.buyerMail = buyerMail;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	@Override
	public boolean isNew() {
		return creationDate == null;
	}
}

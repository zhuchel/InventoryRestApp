package de.magazov.playground.domain;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Domain object for products.
 *
 * @author oleg magazov
 *
 */
public class Product {

    @Id
    private String sku;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 1, max = 200, message
            = "Name must be between 1 and 200 characters")
    private String name;
    @NotNull(message = "Price is mandatory")
    private BigDecimal price;
    private Date creationDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
        setCreationDate(new Date());
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        if (creationDate != null) {
            this.creationDate = creationDate;
        }
    }
}

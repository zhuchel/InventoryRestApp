
package de.magazov.playground.repository;

import de.magazov.playground.domain.Product;
import io.swagger.annotations.Api;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Implementation of {@link MongoRepository} for {@link Product}.
 *
 * @author oleg magazov
 *
 */
@Api(tags = "Product Entity")
@RepositoryRestResource(collectionResourceRel = "products", path = "product")
public interface ProductRepository extends MongoRepository<Product, String> {

}

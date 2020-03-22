
package de.magazov.playground.repository;

import de.magazov.playground.domain.Order;
import io.swagger.annotations.Api;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;

@Api(tags = "Order Entity")
@RepositoryRestResource(collectionResourceRel = "order", path = "order")
public interface OrderRepository extends MongoRepository<Order, String> {

	@Query("{'creationDate': {$gte: ?0, $lte:?1 }}")
	List<Order> findByTimeRange(@Param("from") Date from, @Param("to") Date to);

}

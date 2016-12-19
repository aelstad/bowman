package uk.co.blackpepper.hal.client.test.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import uk.co.blackpepper.hal.client.test.server.model.SimpleEntity;

@RepositoryRestResource(path = "/simple-entities")
public interface SimpleEntityRepository extends CrudRepository<SimpleEntity, Integer> {
}
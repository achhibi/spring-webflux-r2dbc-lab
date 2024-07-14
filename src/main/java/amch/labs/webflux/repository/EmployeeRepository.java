package amch.labs.webflux.repository;

import amch.labs.webflux.domain.Employee;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Integer> {

    Mono<Employee> findById(int id);
}

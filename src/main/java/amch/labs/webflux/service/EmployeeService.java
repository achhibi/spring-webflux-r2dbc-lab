package amch.labs.webflux.service;

import amch.labs.webflux.domain.Employee;
import amch.labs.webflux.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public Flux<Employee> findAll() {
        return employeeRepository.findAll().log();
    }

    public Mono<Employee> findById(int id) {
        return employeeRepository.findById(id)
                .switchIfEmpty(ResponseStatusNotFoundException())
                .log();
    }

    public <T> Mono<T> ResponseStatusNotFoundException() {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    public Mono<Employee> save(Employee Employee) {
        return employeeRepository.save(Employee)
                .log();
    }

    public Mono<Void> update(Employee Employee) {
        return findById(Employee.getId())
                .map(foundEmployee -> Employee.withId(foundEmployee.getId()))
                .flatMap(employeeRepository::save)
                .then();
    }

    public Mono<Void> delete(int id) {
        return findById(id)
                .flatMap(employeeRepository::delete);
    }
}

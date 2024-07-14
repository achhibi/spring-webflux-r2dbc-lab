package amch.labs.webflux.integration;

import amch.labs.webflux.domain.Employee;
import amch.labs.webflux.repository.EmployeeRepository;
import amch.labs.webflux.service.EmployeeService;
import amch.labs.webflux.util.EmployeeCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import(EmployeeService.class)
public class EmployeeControllerIT {

    @MockBean
    private EmployeeRepository employeeRepositoryMock;

    @Autowired
    private WebTestClient testClient;

    private final Employee employee = EmployeeCreator.createValidEmployee();

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }

    @BeforeEach
    public void setUp() {
        BDDMockito.when(employeeRepositoryMock.findAll()).thenReturn(Flux.just(employee));
    }

    @Test
    public void blockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(100); // Use a longer sleep duration
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("Should fail due to blocking operation");

        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError, "Expected BlockingOperationError, but got: " + e.getCause());
        }
    }

    @Test
    @DisplayName("listAll return Flux of Employee when successful")
    public void listAll_ReturnFluxEmployee_WhenSuccessful() {
        testClient.get()
                .uri("/employees")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(employee.getId())
                .jsonPath("$.[0].name").isEqualTo(employee.getName());
    }
}

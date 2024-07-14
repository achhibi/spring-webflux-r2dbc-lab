package amch.labs.webflux.controller;

import amch.labs.webflux.domain.Employee;
import amch.labs.webflux.service.EmployeeService;
import amch.labs.webflux.util.EmployeeCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeServiceMock;

    private final Employee employee = EmployeeCreator.createValidEmployee();

    @BeforeAll
    static void blockHoundSetup() {
        BlockHound.install();
    }


    @BeforeEach
    void setUp() {
        BDDMockito.when(employeeServiceMock.findAll()).thenReturn(Flux.just(employee));
        BDDMockito.when(employeeServiceMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(employee));
        BDDMockito.when(employeeServiceMock.save(EmployeeCreator.createEmployeeToBeSaved())).thenReturn(Mono.just(employee));
        BDDMockito.when(employeeServiceMock.delete(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());
        BDDMockito.when(employeeServiceMock.update(EmployeeCreator.createValidEmployee())).thenReturn(Mono.empty());

    }

    @Test
    void blockHoundWorks() {
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
    @DisplayName("listAll return Flux of employee when successful")
    void listAll_ReturnFluxemployee_WhenSuccessful() {
        StepVerifier.create(employeeController.listAll())
                .expectSubscription()
                .expectNext(employee)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns Mono of employee when successful")
    void findById_ReturnMonoemployee_WhenSuccessful() {
        StepVerifier.create(employeeController.findById(1))
                .expectSubscription()
                .expectNext(employee)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns a Mono with employee when is exist")
    void findById_removeMonoemployee_WhenSuccessful() {
        StepVerifier.create(employeeController.findById(1))
                .expectSubscription()
                .expectNext(employee)
                .verifyComplete();
    }

    @Test
    @DisplayName("save creates an employee when successful")
    void save_Createemployee_WhenSuccessful() {
        Employee employeeToBeSaved = EmployeeCreator.createEmployeeToBeSaved();
        StepVerifier.create(employeeController.save(employeeToBeSaved))
                .expectSubscription()
                .expectNext(employee)
                .verifyComplete();
    }

    @Test
    @DisplayName("delete removes the employee when successful")
    void delete_removesemployee_WhenSuccessful() {
        StepVerifier.create(employeeController.delete(1))
                .expectSubscription()
                .verifyComplete();
    }


    @Test
    @DisplayName("update save update employee and returns empty Mono when successful")
    void update_SaveUpdateemployee_WhenSuccessful() {

        StepVerifier.create(employeeController.update(1, EmployeeCreator.createValidEmployee()))
                .expectSubscription()
                .verifyComplete();
    }


}
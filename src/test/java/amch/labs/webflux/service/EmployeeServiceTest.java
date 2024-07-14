package amch.labs.webflux.service;

import amch.labs.webflux.domain.Employee;
import amch.labs.webflux.repository.EmployeeRepository;
import amch.labs.webflux.util.EmployeeCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService EmployeeService;

    @Mock
    private EmployeeRepository employeeRepositoryMock;

    private final Employee employee = EmployeeCreator.createValidEmployee();

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }


    @BeforeEach
    public void setUp() {
        BDDMockito.when(employeeRepositoryMock.findAll()).thenReturn(Flux.just(employee));
        BDDMockito.when(employeeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(employee));
        BDDMockito.when(employeeRepositoryMock.save(EmployeeCreator.createEmployeeToBeSaved())).thenReturn(Mono.just(employee));
        BDDMockito.when(employeeRepositoryMock.delete(ArgumentMatchers.any(Employee.class))).thenReturn(Mono.empty());
        BDDMockito.when(employeeRepositoryMock.save(EmployeeCreator.createValidEmployee())).thenReturn(Mono.empty());

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
    @DisplayName("findAll return Flux of Employee when successful")
    public void findAll_ReturnFluxEmployee_WhenSuccessful() {
        StepVerifier.create(EmployeeService.findAll())
                .expectSubscription()
                .expectNext(employee)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns Mono of Employee when successful")
    public void findById_ReturnMonoAEmployee_WhenSuccessful() {
        StepVerifier.create(EmployeeService.findById(1))
                .expectSubscription()
                .expectNext(employee)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns Mono error of Employee when dosn't exist")
    public void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(employeeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());
        StepVerifier.create(EmployeeService.findById(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("save creates an Employee when successful")
    public void save_CreateEmployee_WhenSuccessful() {
        Employee employeeToBeSaved = EmployeeCreator.createEmployeeToBeSaved();
        StepVerifier.create(EmployeeService.save(employeeToBeSaved))
                .expectSubscription()
                .expectNext(employee)
                .verifyComplete();
    }

    @Test
    @DisplayName("delete removes the Employee when successful")
    public void delete_removesEmployee_WhenSuccessful() {
        StepVerifier.create(EmployeeService.delete(1))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("delete removes Mono error when dosn't exist")
    public void delete_removesMonoError_WhenEmptyIsReturned() {
        BDDMockito.when(employeeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());
        StepVerifier.create(EmployeeService.delete(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("update save update employee and returns empty Mono when successful")
    public void update_SaveUpdateEmployee_WhenSuccessful() {

        StepVerifier.create(EmployeeService.update(EmployeeCreator.createValidEmployee()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("update returns Mono error when dosn't exist")
    public void update_ReturnsMonoError_WhenEmptyIsReturned() {
        BDDMockito.when(employeeRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());
        StepVerifier.create(EmployeeService.update(EmployeeCreator.createValidEmployee()))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

}
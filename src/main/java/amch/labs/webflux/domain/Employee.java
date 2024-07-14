package amch.labs.webflux.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Table("employee")
public class Employee {
    @Id
    private Integer id;
    @NotNull
    @NotEmpty(message = "the name cannot be empty")
    private String name;

    @NotNull
    @NotEmpty(message = "the address cannot be empty")
    private String address;

    @NotNull
    private LocalDate dob;

    @NotNull
    private String position;

    @NotNull
    private BigDecimal salary;

    @NotNull
    private String department;
}


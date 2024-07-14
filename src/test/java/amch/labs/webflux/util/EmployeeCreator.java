package amch.labs.webflux.util;

import amch.labs.webflux.domain.Employee;

public class EmployeeCreator {
    public static Employee createEmployeeToBeSaved() {
        return Employee.builder()
                .name("One Piece")
                .build();
    }

    public static Employee createValidEmployee() {
        return Employee.builder()
                .id(1)
                .name("One Piece")
                .build();
    }
    /**
     * Creates a valid updated Employee object.
     *
     * @return the updated Employee object
     */
    public static Employee createdValidUpdateEmployee() {
        return Employee.builder()
                .id(1)
                .name("One Piece 2")
                .build();
    }
}

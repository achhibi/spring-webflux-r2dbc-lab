CREATE TABLE employee
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255)   NOT NULL,
    address    VARCHAR(255)   NOT NULL,
    dob        DATE           NOT NULL,
    position   VARCHAR(255)   NOT NULL,
    salary     DECIMAL(19, 4) NOT NULL,
    department VARCHAR(255)   NOT NULL
);
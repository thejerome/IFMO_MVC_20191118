package com.efimchik.ifmo.web.mvc.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name = "EMPLOYEE")
public class Employee {
    @Id
    private Long id;

    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "firstName", column = @Column(name = "FIRSTNAME")),
            @AttributeOverride(name = "lastName", column = @Column(name = "LASTNAME")),
            @AttributeOverride(name = "middleName", column = @Column(name = "MIDDLENAME")),
    })
    private FullName fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "POSITION")
    private Position position;

    @Column(name = "HIREDATE")
    private LocalDate hired;

    @Column(name = "SALARY")
    private BigDecimal salary;

    @OneToOne(cascade = CascadeType.ALL, targetEntity = Employee.class)
    @JoinColumn(name = "MANAGER", referencedColumnName = "ID")
    private Employee manager;


    public Employee() {
    }

    @OneToOne(cascade = CascadeType.ALL, targetEntity = Department.class)
    @JoinColumn(name = "DEPARTMENT", referencedColumnName = "ID")
    private Department department;

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(FullName fullName) {
        this.fullName = fullName;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setHired(LocalDate hired) {
        this.hired = hired;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @JsonCreator
    public Employee(@JsonProperty("id") final Long id,
                    @JsonProperty("fullName") final FullName fullName,
                    @JsonProperty("position") final Position position,
                    @JsonProperty("hired") final LocalDate hired,
                    @JsonProperty("salary") final BigDecimal salary,
                    @JsonProperty("manager") final Employee manager,
                    @JsonProperty("department") final Department department) {
        this.id = id;
        this.fullName = fullName;
        this.position = position;
        this.hired = hired;
        this.salary = salary.setScale(5, RoundingMode.HALF_UP);
        this.manager = manager;
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public FullName getFullName() {
        return fullName;
    }

    public Position getPosition() {
        return position;
    }

    public LocalDate getHired() {
        return hired;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public Employee getManager() {
        return manager;
    }

    public Department getDepartment() {
        return department;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Employee employee = (Employee) o;
        return Objects.equal(id, employee.id) &&
                Objects.equal(fullName, employee.fullName) &&
                position == employee.position &&
                Objects.equal(hired, employee.hired) &&
                Objects.equal(salary, employee.salary) &&
                Objects.equal(manager, employee.manager) &&
                Objects.equal(department, employee.department);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, fullName, position, hired, salary, manager, department);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("fullName", fullName)
                .add("position", position)
                .add("hired", hired)
                .add("salary", salary)
                .add("manager", manager)
                .add("department", department)
                .toString();
    }

    public static class Parser {
        private static ObjectMapper mapper = new ObjectMapper();

        static {
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        }

        public static String toJson(Employee employee) {
            try {
                final StringWriter writer = new StringWriter();
                mapper.writeValue(writer, employee);
                return writer.toString();
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        }

        public static Employee parseJson(String json) {
            try {
                return mapper.readValue(json, Employee.class);
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        }
    }
}

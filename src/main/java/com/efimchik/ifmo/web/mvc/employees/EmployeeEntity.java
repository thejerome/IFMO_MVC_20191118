package com.efimchik.ifmo.web.mvc.employees;

import com.efimchik.ifmo.web.mvc.departments.DepartmentEntity;
import com.efimchik.ifmo.web.mvc.domain.Position;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "employee")
public class EmployeeEntity {

    @Id @SequenceGenerator(name = "employee_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_seq")
    private final Long id;

    @Column(name = "firstname")
    private final String firstName;

    @Column(name = "lastname")
    private final String lastName;

    @Column(name = "middlename")
    private final String middleName;

    @Enumerated(EnumType.STRING)
    private final Position position;

    @JoinColumn(name = "manager")
    @OneToOne(fetch = FetchType.LAZY)
    private final EmployeeEntity manager;

    @Column(name = "hiredate")
    private final LocalDate hireDate;

    private final BigDecimal salary;

    @ManyToOne @JoinColumn(name = "department")
    private final DepartmentEntity department;

    public EmployeeEntity() {
        id = null;
        firstName = null;
        lastName = null;
        middleName = null;
        position = null;
        manager = null;
        hireDate = null;
        salary = null;
        department = null;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public Position getPosition() {
        return position;
    }

    public EmployeeEntity getManager() {
        return manager;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public DepartmentEntity getDepartment() {
        return department;
    }
}

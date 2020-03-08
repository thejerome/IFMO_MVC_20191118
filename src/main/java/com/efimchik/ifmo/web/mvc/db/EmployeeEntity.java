package com.efimchik.ifmo.web.mvc.db;

import com.efimchik.ifmo.web.mvc.domain.Position;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "employee")
public class EmployeeEntity {

    @Id
    private final Long id;

    private final String firstname;

    private final String middlename;

    private final String lastname;

    @Enumerated(EnumType.STRING)
    private final Position position;

    private final LocalDate hiredate;

    private final BigDecimal salary;

    @ManyToOne
    @JoinColumn(name = "manager")
    private final EmployeeEntity manager;

    @ManyToOne
    @JoinColumn(name = "department")
    private final DepartmentEntity department;

    public EmployeeEntity() {
        id = null;
        firstname = null;
        middlename = null;
        lastname = null;
        position = null;
        hiredate = null;
        salary = null;
        manager = null;
        department = null;
    }

    public EmployeeEntity(EmployeeEntity manager) {
        id = null;
        firstname = null;
        middlename = null;
        lastname = null;
        position = null;
        hiredate = null;
        salary = null;
        this.manager = manager;
        department = null;
    }

    public EmployeeEntity(DepartmentEntity department) {
        id = null;
        firstname = null;
        middlename = null;
        lastname = null;
        position = null;
        hiredate = null;
        salary = null;
        manager = null;
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public Position getPosition() {
        return position;
    }

    public LocalDate getHiredate() {
        return hiredate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public EmployeeEntity getManager() {
        return manager;
    }

    public DepartmentEntity getDepartment() {
        return department;
    }
}

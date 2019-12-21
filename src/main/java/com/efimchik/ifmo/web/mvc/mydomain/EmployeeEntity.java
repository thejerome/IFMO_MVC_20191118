package com.efimchik.ifmo.web.mvc.mydomain;


import com.efimchik.ifmo.web.mvc.domain.Position;
import com.efimchik.ifmo.web.mvc.repository.DepRepository;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "EMPLOYEE")
public class EmployeeEntity {

    @Transient
    DepRepository depRepository;

    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String middlename;
    @Enumerated(EnumType.STRING)
    private Position position;
    private LocalDate hiredate;
    private BigDecimal salary;
    private Long manager;
    private Long department;

    public EmployeeEntity() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public LocalDate getHired() {
        return hiredate;
    }

    public void setHired(LocalDate hired) {
        this.hiredate = hired;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Long getManager() {
        return manager;
    }

    public void setManager(Long manager) {
        this.manager = manager;
    }

    public Long getDepartment() {
        return department;
    }

    public void setDepartment(Long department) {
        this.department = department;
    }


}

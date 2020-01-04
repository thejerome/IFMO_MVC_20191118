package com.efimchik.ifmo.web.mvc.repos;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name="employee")
public class RepEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String firstname;
    @Column(name="lastname")
    private String lastName;
    private String middlename;
    private String position;
    private Integer manager;
    @Column(name="hiredate")
    private Date hired;
    private Double salary;
    private Integer department;

    public Integer getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastName;
    }

    public String getMiddlename() { return middlename; }

    public String getPosition() {
        return position;
    }

    public Integer getManager() {
        return manager;
    }

    public Date getHiredate() {
        return hired;
    }

    public Double getSalary() {
        return salary;
    }

    public Integer getDepartment() {
        return department;
    }
}

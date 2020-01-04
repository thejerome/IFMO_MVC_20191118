package com.efimchik.ifmo.web.mvc.repos;

import javax.persistence.*;

@Entity
@Table(name="department")
public class RepDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String location;

    public RepDepartment(){
    }

    public Integer getId() {
        return id;
    }

    public String getName() { return name; }

    public String getLocation() { return location; }
}

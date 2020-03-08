package com.efimchik.ifmo.web.mvc.db;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "department")
public class DepartmentEntity {

    @Id
    private final Long id;
    private final String name;
    private final String location;

    public DepartmentEntity() {
        id = null;
        name = null;
        location = null;
    }

    public DepartmentEntity(String name) {
        id = null;
        this.name = name;
        location = null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}

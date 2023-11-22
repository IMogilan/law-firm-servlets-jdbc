package com.mogilan.servlet.dto;

import java.util.List;
import java.util.Objects;

public class ClientDto {
    private Long id;
    private String name;
    private String description;
    private List<SimpleTaskDto> tasks;

    public ClientDto() {
    }

    public ClientDto(Long id, String name, String description, List<SimpleTaskDto> tasks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tasks = tasks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SimpleTaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<SimpleTaskDto> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "ClientDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDto clientDto = (ClientDto) o;
        return Objects.equals(getId(), clientDto.getId()) && Objects.equals(getName(), clientDto.getName()) && Objects.equals(getDescription(), clientDto.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription());
    }
}

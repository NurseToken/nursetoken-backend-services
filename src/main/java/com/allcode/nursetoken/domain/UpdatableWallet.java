package com.allcode.nursetoken.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdatableWallet {

    @Id
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    @Size(min = 1, max = 50)
    private String name;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
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

    @Override
    public String toString() {
        return "UpdatableWallet{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}

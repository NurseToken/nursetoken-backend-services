package com.allcode.nursetoken.domain;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ImportableWallet {

    @Id
    private Long id;

    @NotNull
    @NotBlank
    private String wif;

    @NotNull
    @NotBlank
    private String name;


    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWif() {
        return wif;
    }


    public ImportableWallet wif(String wif) {
        this.wif = wif;
        return this;
    }

    public void setWif(String wif) {
        this.wif = wif;
    }

    public ImportableWallet name(String name) {
        this.name = name;
        return this;
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
            ", wif='" + wif + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}

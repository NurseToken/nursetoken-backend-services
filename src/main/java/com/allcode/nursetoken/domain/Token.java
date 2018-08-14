package com.allcode.nursetoken.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Token.
 */
@Entity
@Table(name = "token")
public class Token implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "contract_address", nullable = false)
    private String contractAddress;

    @NotNull
    @Column(name = "decimals", nullable = false)
    private Integer decimals;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "script_hash", nullable = false)
    private String scriptHash;

    @NotNull
    @Column(name = "symbol", nullable = false)
    private String symbol;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public Token contractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
        return this;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public Token decimals(Integer decimals) {
        this.decimals = decimals;
        return this;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public String getName() {
        return name;
    }

    public Token name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScriptHash() {
        return scriptHash;
    }

    public Token scriptHash(String scriptHash) {
        this.scriptHash = scriptHash;
        return this;
    }

    public void setScriptHash(String scriptHash) {
        this.scriptHash = scriptHash;
    }

    public String getSymbol() {
        return symbol;
    }

    public Token symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Token token = (Token) o;
        if (token.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), token.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Token{" +
            "id=" + getId() +
            ", contractAddress='" + getContractAddress() + "'" +
            ", decimals=" + getDecimals() +
            ", name='" + getName() + "'" +
            ", scriptHash='" + getScriptHash() + "'" +
            ", symbol='" + getSymbol() + "'" +
            "}";
    }
}

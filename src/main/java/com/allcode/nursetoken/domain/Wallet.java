package com.allcode.nursetoken.domain;

import com.allcode.nursetoken.service.util.CryptUtils;
import com.allcode.nursetoken.service.util.MiddlewareRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Wallet.
 */
@Entity
@Table(name = "wallet")
public class Wallet extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "address", nullable = false)
    private String address;

    @NotNull
    @Column(name = "encripted_private_key", nullable = false, updatable = false)
    @JsonIgnore
    private String encriptedPrivateKey;

    @NotNull
    @Column(name = "encripted_public_key", nullable = false, updatable = false)
    @JsonIgnore
    private String encriptedPublicKey;

    @NotNull
    @Column(name = "encripted_public_key_hash", nullable = false, updatable = false)
    @JsonIgnore
    private String encriptedPublicKeyHash;

    @NotNull
    @Column(name = "encripted_wif", nullable = false, updatable = false)
    @JsonIgnore
    private String encriptedWif;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("")
    private User owner;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public Wallet address(String address) {
        this.address = address;
        return this;
    }

    public static Wallet createFromApiNeo(User user){
        String key = System.getenv("PASSPHRASE_VALUE");
        String url = System.getenv("NEO_API_URL") + "/wallet/new";
        JSONObject response = MiddlewareRequest.post(url, null);

        if(!response.has("address")){
            return null;
        }

        CryptUtils.encrypt(response.getString("address"), key);
        Wallet wallet = new Wallet();
        wallet.setAddress(response.getString("address"));
        wallet.setEncriptedPrivateKey(CryptUtils.encrypt(response.getString("private_key"), key));
        wallet.setEncriptedPublicKey(CryptUtils.encrypt(response.getString("public_key"), key));
        wallet.setEncriptedPublicKeyHash(CryptUtils.encrypt(response.getString("public_key_hash"), key));
        wallet.setEncriptedWif(CryptUtils.encrypt(response.getString("wif"), key));
        wallet.setOwner(user);
        wallet.setName(user.getFirstName() + "'s Wallet");
        return wallet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEncriptedPrivateKey() {
        return encriptedPrivateKey;
    }

    public String getPrivateKey() {
        if(encriptedPrivateKey == null){
            return null;
        }
        return CryptUtils.decrypt(encriptedPrivateKey, System.getenv("PASSPHRASE_VALUE"));
    }

    public Wallet encriptedPrivateKey(String encriptedPrivateKey) {
        this.encriptedPrivateKey = encriptedPrivateKey;
        return this;
    }

    public void setEncriptedPrivateKey(String encriptedPrivateKey) {
        this.encriptedPrivateKey = encriptedPrivateKey;
    }

    public String getEncriptedPublicKey() {
        return encriptedPublicKey;
    }

    public String getPublicKey() {
        if(encriptedPublicKey == null){
            return null;
        }
        return CryptUtils.decrypt(encriptedPublicKey, System.getenv("PASSPHRASE_VALUE"));
    }

    public Wallet encriptedPublicKey(String encriptedPublicKey) {
        this.encriptedPublicKey = encriptedPublicKey;
        return this;
    }

    public void setEncriptedPublicKey(String encriptedPublicKey) {
        this.encriptedPublicKey = encriptedPublicKey;
    }

    public String getEncriptedPublicKeyHash() {
        return encriptedPublicKeyHash;
    }

    public String getPublicKeyHash() {
        if(encriptedPublicKeyHash == null){
            return null;
        }
        return CryptUtils.decrypt(encriptedPublicKeyHash, System.getenv("PASSPHRASE_VALUE"));
    }

    public Wallet encriptedPublicKeyHash(String encriptedPublicKeyHash) {
        this.encriptedPublicKeyHash = encriptedPublicKeyHash;
        return this;
    }

    public void setEncriptedPublicKeyHash(String encriptedPublicKeyHash) {
        this.encriptedPublicKeyHash = encriptedPublicKeyHash;
    }

    public String getEncriptedWif() {
        return encriptedWif;
    }

    public String getWif() {
        if(encriptedWif == null){
            return null;
        }
        return CryptUtils.decrypt(encriptedWif, System.getenv("PASSPHRASE_VALUE"));
    }

    public Wallet encriptedWif(String encriptedWif) {
        this.encriptedWif = encriptedWif;
        return this;
    }

    public void setEncriptedWif(String encriptedWif) {
        this.encriptedWif = encriptedWif;
    }

    public User getOwner() {
        return owner;
    }

    public Wallet owner(User user) {
        this.owner = user;
        return this;
    }

    public void setOwner(User user) {
        this.owner = user;
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
        Wallet wallet = (Wallet) o;
        if (wallet.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), wallet.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Wallet{" +
            "id=" + getId() +
            ", address='" + getAddress() + "'" +
            ", privateKey='" + getPrivateKey() + "'" +
            ", publicKey='" + getPublicKey() + "'" +
            ", publicKeyHash='" + getPublicKeyHash() + "'" +
            ", wif='" + getWif() + "'" +
            "}";
    }
}

package com.allcode.nursetoken.domain;

import com.allcode.nursetoken.service.util.CryptUtils;
import com.allcode.nursetoken.service.util.MiddlewareRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @Column(name = "address", nullable = false)
    private String address;

    @NotNull
    @Column(name = "private_key", nullable = false)
    private String privateKey;

    @NotNull
    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @NotNull
    @Column(name = "public_key_hash", nullable = false)
    private String publicKeyHash;

    @NotNull
    @Column(name = "wif", nullable = false)
    private String wif;

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
        Wallet wallet = new Wallet();
        wallet.setAddress(response.getString("address"));
        wallet.setPrivateKey(CryptUtils.encrypt(response.getString("private_key"), key));
        wallet.setPublicKey(CryptUtils.encrypt(response.getString("public_key"), key));
        wallet.setPublicKeyHash(CryptUtils.encrypt(response.getString("public_key_hash"), key));
        wallet.setWif(CryptUtils.encrypt(response.getString("wif"), key));
        wallet.setOwner(user);
        return wallet;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public Wallet privateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public Wallet publicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKeyHash() {
        return publicKeyHash;
    }

    public Wallet publicKeyHash(String publicKeyHash) {
        this.publicKeyHash = publicKeyHash;
        return this;
    }

    public void setPublicKeyHash(String publicKeyHash) {
        this.publicKeyHash = publicKeyHash;
    }

    public String getWif() {
        return wif;
    }

    public Wallet wif(String wif) {
        this.wif = wif;
        return this;
    }

    public void setWif(String wif) {
        this.wif = wif;
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

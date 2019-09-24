package com.payments.account.model;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "Account")
@NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a")
@NamedQuery(name = "Account.findByEmail", query = "SELECT a FROM Account a WHERE a.email = :email")
@Schema(name="Account", description="POJO that represents the account contents.")
public class Account {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column
    @Schema
    private int id;
    @Column
    @Email
    @Schema(required = true, format = "email")
    private String email;
    @Column
    @PositiveOrZero
    @Schema
    private BigDecimal balance;

    public Account(int id, String email, BigDecimal balance) {
        this.id = id;
        this.email = email;
        this.balance = balance;
    }

    public Account(String email, BigDecimal balance) {
        this.email = email;
        this.balance = balance;
    }

    public Account() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Account account = (Account) o;
        return id == account.id && Objects.equals(email, account.email) && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, balance);
    }

    @Override
    public String toString() {
        return "Account{" + "id=" + id + ", email='" + email + '\'' + ", balance=" + balance + '}';
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }
}

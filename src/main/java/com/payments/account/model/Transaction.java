package com.payments.account.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class Transaction {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column
    private int id;
    @OneToOne
    private Account debit;
    @OneToOne
    private Account credit;
    @Column
    private BigDecimal amount;
    @Column
    @NotNull
    private Operation operation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getDebit() {
        return debit;
    }

    public void setDebit(Account debit) {
        this.debit = debit;
    }

    public Account getCredit() {
        return credit;
    }

    public void setCredit(Account credit) {
        this.credit = credit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "Transaction{" + "id=" + id + ", debit=" + debit + ", credit=" + credit + ", amount=" + amount + ", " +
                "operation=" + operation + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return id == that.id && Objects.equals(debit, that.debit) && Objects.equals(credit, that.credit) && Objects.equals(amount, that.amount) && operation == that.operation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, debit, credit, amount, operation);
    }
}

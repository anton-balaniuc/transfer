package com.payments.account.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "AccountTransaction")
@NamedQuery(name = "Transaction.findAll", query = "SELECT t FROM Transaction t")
public class Transaction {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column
    private int id;
    @ManyToOne
    @JoinColumn
    private Account debit;
    @OneToOne
    @JoinColumn
    private Account credit;
    @Column
    private BigDecimal amount;
    @Column
    @NotNull
    private TransactionType transactionType;

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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String toString() {
        return "Transaction{" + "id=" + id + ", debit=" + debit + ", credit=" + credit + ", amount=" + amount + ", " +
                "operation=" + transactionType + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return id == that.id && Objects.equals(debit, that.debit) && Objects.equals(credit, that.credit) && Objects.equals(amount, that.amount) && transactionType == that.transactionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, debit, credit, amount, transactionType);
    }
}

package com.payments.account.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "AccountTransaction")
@NamedQuery(name = "Transaction.findAll", query = "SELECT t FROM Transaction t")
@NamedQuery(name = "Transaction.findByAccount", query = "SELECT t FROM Transaction t where t.from.id = :accountId or t.to.id = :accountId")
@Schema(name="Transaction", description="POJO that represents the transactions contents.")
public class Transaction {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column
    @Schema
    private int id;
    @ManyToOne
    @JoinColumn
    @Schema
    private Account from;
    @OneToOne
    @JoinColumn
    @Schema
    private Account to;
    @Column
    @Schema
    private BigDecimal amount;
    @Column
    @NotNull
    @Schema
    private TransactionType transactionType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getFrom() {
        return from;
    }

    public void setFrom(Account from) {
        this.from = from;
    }

    public Account getTo() {
        return to;
    }

    public void setTo(Account to) {
        this.to = to;
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
        return "Transaction{" + "id=" + id + ", debit=" + from + ", credit=" + to + ", amount=" + amount + ", " +
                "operation=" + transactionType + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return id == that.id && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(amount, that.amount) && transactionType == that.transactionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, amount, transactionType);
    }


    public static class Builder {

        private Account from;
        private Account to;
        private BigDecimal amount;
        private TransactionType transactionType;

        public Builder() {
        }

        Builder(Account from, Account to, BigDecimal amount, TransactionType transactionType) {
            this.from = from;
            this.to = to;
            this.amount = amount;
            this.transactionType = transactionType;
        }

        public Builder from(Account from){
            this.from = from;
            return Builder.this;
        }

        public Builder to(Account to){
            this.to = to;
            return Builder.this;
        }

        public Builder amount(BigDecimal amount){
            this.amount = amount;
            return Builder.this;
        }

        public Builder transactionType(TransactionType transactionType){
            this.transactionType = transactionType;
            return Builder.this;
        }

        public Transaction build() {
            Transaction transaction = new Transaction();
            transaction.setAmount(this.amount);
            transaction.setFrom(this.from);
            transaction.setTo(this.to);
            transaction.setTransactionType(this.transactionType);
            return transaction;
        }
    }

}

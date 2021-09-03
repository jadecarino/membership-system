package synoptic.project.rest.account;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "Account")
@NamedQuery(name = "Account.findAll", query = "SELECT e FROM Account e")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cardNumber")
    private String cardNumber;

    @Column(name = "balance")
    private int balance;

    public Account() {
    }

    public Account(String cardNumber, int balance) {
        this.cardNumber = cardNumber;
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
    
}
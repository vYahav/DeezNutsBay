package vanunu.deeznuts;

/**
 * Created by Yahav Vanunu on 28/10/2017.
 */

public class UserClass {
    public String name,address,creditcard,FCMCode,rank,groupID;
    public int balance;
    public boolean productsoldstatus;


    public UserClass()
    {

    }


    public UserClass(String name,String credit,String address,String groupid) {
        this.name = name;
        this.creditcard=credit;
        this.address=address;
        this.balance=0;
        this.FCMCode="null";
        this.rank="User";
        this.productsoldstatus=false;
        this.groupID=groupid;
    }
    public String GetName()
    {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreditcard() {
        return creditcard;
    }

    public void setCreditcard(String creditcard) {
        this.creditcard = creditcard;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String GetFCMCode() {
        return this.FCMCode;
    }

    public void SetFCMCode(String FCMCode) {
        this.FCMCode = FCMCode;
    }

    public void AddToBalance(int x)
    {
        this.balance+=x;
    }
    public void SubstructFromBalance(int x)
    {
        this.balance-=x;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public boolean getProductsoldstatus() {
        return productsoldstatus;
    }

    public void setProductsoldstatus(boolean productsoldstatus) {
        this.productsoldstatus = productsoldstatus;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }
}

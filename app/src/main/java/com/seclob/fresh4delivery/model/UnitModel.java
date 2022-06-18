package com.seclob.fresh4delivery.model;

public class UnitModel {
    private String ID;
    private String PID;
    private String RID;
    private String Name;
    private String Price;
    private String Offer;
    private String IsVeg;
    private String IsCarted;
    private String Qty;
    private String Status;


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }


    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }

    public String getOffer() {
        return Offer;
    }

    public void setOffer(String Offer) {
        this.Offer = Offer;
    }

    public String getIsVeg() {
        return IsVeg;
    }

    public void setIsVeg(String IsVeg) {
        this.IsVeg = IsVeg;
    }

    public String getIsCarted() {
        return IsCarted;
    }

    public void setIsCarted(String IsCarted) {
        this.IsCarted = IsCarted;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String Qty) {
        this.Qty = Qty;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

}
    
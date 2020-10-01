package com.ssl.sspls.billingapp.Model;

import java.util.ArrayList;

public class PaymentHeadList {
    ArrayList<PaymentDetailList>  List;
    String headname;
    public PaymentHeadList(String MenuName, ArrayList<PaymentDetailList> child)
    {
        this.headname=MenuName;
        this.List=child;
    }
    public ArrayList<PaymentDetailList> getList() {
        return List;
    }
    public void setList(ArrayList<PaymentDetailList> list) {
        List = list;
    }
    public String getHeadname() {
        return headname;
    }
    public void setHeadname(String headname) {
        this.headname = headname;
    }
}

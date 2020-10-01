package com.ssl.sspls.billingapp.Model;

public class PaymentDetailListType {
    String srNo,modeName,modeType,Amount;
    public PaymentDetailListType(String srNo,String modeName,String modeType,String Amount)
    {
        this.srNo=srNo;
        this.modeName=modeName;
        this.modeType=modeType;
        this.Amount=Amount;
    }
    public String getSrNo() {
        return srNo;
    }

    public String getModeName() {
        return modeName;
    }

    public String getModeType() {
        return modeType;
    }

    public String getAmount() {
        return Amount;
    }


}

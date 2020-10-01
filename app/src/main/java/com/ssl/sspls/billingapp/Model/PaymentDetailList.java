package com.ssl.sspls.billingapp.Model;
public class PaymentDetailList {
    String PayMode;
    String Mode;
    public PaymentDetailList(String PayMode, String Mode)
    {
        this.PayMode=PayMode;
        this.Mode=Mode;
    }
    public String getPayMode() {
        return PayMode;
    }
    public void setPayMode(String payMode) {
        PayMode = payMode;
    }
    public String getMode() {
        return Mode;
    }
    public void setMode(String mode) {
        Mode = mode;
    }
}

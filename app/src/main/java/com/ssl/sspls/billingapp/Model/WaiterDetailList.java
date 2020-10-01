package com.ssl.sspls.billingapp.Model;

public class WaiterDetailList {
    String Cr_Code,Cr_Name,Password;
    public WaiterDetailList(String Cr_Code, String Cr_Name, String Password)
    {
        this.Cr_Code =Cr_Code;
        this.Cr_Name =Cr_Name;
        this.Password =Password;
    }
    public String getCr_Code() {
        return Cr_Code;
    }
    public void setCr_Code(String cr_Code) {
        Cr_Code = cr_Code;
    }
    public String getCr_Name() {
        return Cr_Name;
    }
    public void setCr_Name(String cr_Name) {
        Cr_Name = cr_Name;
    }
    public String getPassword() {
        return Password;
    }
    public void setPassword(String password) {
        Password = password;
    }
}

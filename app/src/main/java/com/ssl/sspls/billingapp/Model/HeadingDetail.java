package com.ssl.sspls.billingapp.Model;

public class HeadingDetail {
    String Heading,Postion;
    public HeadingDetail(String Heading,String Postion)
    {
        this.Heading=Heading;
        this.Postion=Postion;
    }
    public String getHeading() {
        return Heading;
    }
    public void setHeading(String heading) {
        Heading = heading;
    }
    public String getPostion() {
        return Postion;
    }
    public void setPostion(String postion) {
        Postion = postion;
    }
}

package com.seclob.fresh4delivery.libraries;

public class SmsContents {

    public String OtpContent(String Otp,String InstitutionName)
    {
        String Msg = Otp+" is OTP for your "+InstitutionName+" App, OTP valid for 15 minutes. " +
                    "Do not share this OTP with anyone.\n" +
                    "Regards,\n" +
                    "eBS Education ";
        return Msg;
    }

}

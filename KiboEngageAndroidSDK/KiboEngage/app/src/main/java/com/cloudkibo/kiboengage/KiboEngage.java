package com.cloudkibo.kiboengage;

/**
 * Created by sojharo on 09/09/2016.
 */
public class KiboEngage {

    private static String appId;

    private static String clientId;

    private static String appSecret;

    private static String customerName;

    private static String customerEmail;

    public static void initialize(String app_id, String client_id, String app_secret, String customer_name, String customer_email){

        appId = app_id;
        clientId = client_id;
        appSecret = app_secret;
        customerName = customer_name;
        customerEmail = customer_email;

    }

    public static String getCustomerName(){
        return customerName;
    }

    public static String getCustomerEmail(){
        return customerEmail;
    }

    public static void setCustomerName(String name){
        customerName = name;
    }

    public static void setCustomerEmail(String email){
        customerEmail = email;
    }

}

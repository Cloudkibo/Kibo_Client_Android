package com.cloudkibo.kiboengage;

/**
 * Created by sojharo on 09/09/2016.
 */
public class KiboEngage {

    private static String appId;

    private static String appKey;

    private static String appSecret;

    private static String customerName;

    private static String customerEmail;

    public static void initialize(String id, String key, String secret, String name, String email){

        appId = id;
        appKey = key;
        appSecret = secret;
        customerName = name;
        customerEmail = email;

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

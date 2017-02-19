package com.fantasticfive.shareback.concept2.exception;

/**
 * Created by sagar on 19/2/17.
 */
public class NoInternetException extends Exception {
    public NoInternetException() {
        super("No Internet Connection Detected, Please Connect to Internet");
    }
}

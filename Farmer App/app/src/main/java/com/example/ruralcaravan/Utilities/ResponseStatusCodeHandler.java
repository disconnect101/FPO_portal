package com.example.ruralcaravan.Utilities;

public class ResponseStatusCodeHandler {

    public static boolean isSuccessful(String id) {
        return id.equals("0");
    }
    public static String getMessage(String id) {
        switch (id) {
            case "0": return "Success";
            case "1": return "Invalid phone number";
            case "2": return "OTP couldn't be sent";
            case "3": return "OTP couldn't be saved in database";
            case "4": return "Invalid OTP entered";
            case "5": return "Invalid request";
            case "6": return "OTP expired";
            case "7": return "Unable to register user";
            case "8": return "Number already registered";
            case "9": return "Invalid credentials";
            case "10": return "Contact number not verified";
        }
        return "Android system error";
    }
}

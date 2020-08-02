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
            case "11": return "Product not available";
            case "12": return "Unable to access database";
            case "13": return "Kart Item not available";
            case "14": return "Unable to delete cart item";
            case "15": return "Order couldn't be placed";
            case "16": return "Transaction failed";
            case "17": return "Insufficient funds";
            case "18": return "Unable to update cart";
            case "19": return "Crop not available";
            case "20": return "Unauthorized access";
            case "21": return "Arguments missing";
            case "22": return "No entry in farmer table";
            case "23": return "No meeting token found";
            case "24": return "No matching meeting token found";
        }
        return "Android system error";
    }
}
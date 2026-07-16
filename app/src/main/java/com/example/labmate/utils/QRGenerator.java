package com.example.labmate.utils;

public class QRGenerator {
    public static String generateId(long number){
        return String.format("EQ%06d", number);
    }
}

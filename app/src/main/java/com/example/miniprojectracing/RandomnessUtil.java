package com.example.miniprojectracing;

public class RandomnessUtil {

    public static int getRandomHorse() {
        // Simulate a random horse race outcome between horse 1, 2, or 3
        return (int) (Math.random() * 3) + 1;
    }
}


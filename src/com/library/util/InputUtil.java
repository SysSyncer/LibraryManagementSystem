package com.library.util;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputUtil {
    private static Scanner scanner = new Scanner(System.in);

    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                return input;
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter an integer.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
    }
    
    public static void closeScanner(){
        if(scanner != null){
            scanner.close();
        }
    }
}
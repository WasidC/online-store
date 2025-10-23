package com.pluralsight;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Store {

    public static void main(String[] args) {
        ArrayList<Product> inventory = new ArrayList<>();
        ArrayList<Product> cart = new ArrayList<>();

        loadInventory("products.csv", inventory);

        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice != 3) {
            System.out.println("\nWelcome to the Online Store!");
            System.out.println("1. Show Products");
            System.out.println("2. Show Cart");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter 1, 2, or 3.");
                scanner.nextLine();
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // clear newline

            switch (choice) {
                case 1 -> displayProducts(inventory, cart, scanner);
                case 2 -> displayCart(cart, scanner);
                case 3 -> System.out.println("Thank you for shopping with us!");
                default -> System.out.println("Invalid choice!");
            }
        }
        scanner.close();
    }

    // -------------------------------------------------------
    // Load products from CSV file
    // -------------------------------------------------------
    public static void loadInventory(String fileName, ArrayList<Product> inventory) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("SKU")) continue; // skip headers
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    inventory.add(new Product(id, name, price));
                }
            }
            System.out.println("Loaded " + inventory.size() + " products.");
        } catch (IOException e) {
            System.out.println("Error loading inventory: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Display products and allow adding to cart
    // -------------------------------------------------------
    public static void displayProducts(ArrayList<Product> inventory,
                                       ArrayList<Product> cart,
                                       Scanner scanner) {
        System.out.println("\n=== Product List ===");
        for (Product p : inventory) {
            System.out.println(p);
        }

        System.out.print("\nEnter product ID to add to cart (or X to go back): ");
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("X")) return;

        Product selected = findProductById(input, inventory);
        if (selected != null) {
            cart.add(selected);
            System.out.println(selected.getName() + " added to your cart!");
        } else {
            System.out.println("Product not found. Try again.");
        }
    }

    // -------------------------------------------------------
    // Display cart contents, total, and offer checkout
    // -------------------------------------------------------
    public static void displayCart(ArrayList<Product> cart, Scanner scanner) {
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }

        System.out.println("\n=== Your Cart ===");
        double total = 0.0;
        for (Product p : cart) {
            System.out.println(p);
            total += p.getPrice();
        }
        System.out.printf("Total: $%.2f\n", total);

        System.out.print("Enter C to Checkout or X to go back: ");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("C")) {
            checkOut(cart, total, scanner);
        }
    }

    // -------------------------------------------------------
    // Checkout process
    // -------------------------------------------------------
    public static void checkOut(ArrayList<Product> cart,
                                double totalAmount,
                                Scanner scanner) {
        System.out.printf("\nYour total is $%.2f\n", totalAmount);
        System.out.print("Enter payment amount: ");

        double payment = 0;
        try {
            payment = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to menu.");
            return;
        }

        if (payment < totalAmount) {
            System.out.println("Insufficient funds. Transaction cancelled.");
            return;
        }

        double change = payment - totalAmount;
        System.out.printf("Payment accepted. Your change: $%.2f\n", change);

        // Print receipt
        System.out.println("\n=== RECEIPT ===");
        for (Product p : cart) {
            System.out.println(p);
        }
        System.out.printf("TOTAL: $%.2f\n", totalAmount);
        System.out.printf("CASH: $%.2f\n", payment);
        System.out.printf("CHANGE: $%.2f\n", change);
        System.out.println("Thank you for shopping with us!\n");

        // Clear cart
        cart.clear();
    }

    // -------------------------------------------------------
    // Find product by ID helper
    // -------------------------------------------------------
    public static Product findProductById(String id, ArrayList<Product> inventory) {
        for (Product p : inventory) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }
}
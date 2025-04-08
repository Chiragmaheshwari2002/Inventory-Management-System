package project;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class InventoryManagementSystem {

    public void stock(int id, String name, int quantity, String expiryDate) {
        String checkQuery = "SELECT quantity FROM product_batches WHERE id = ? AND expiry_date = ?";
        String updateQuery = "UPDATE product_batches SET quantity = quantity + ? WHERE id = ? AND expiry_date = ?";
        String insertQuery = "INSERT INTO product_batches (id, name, quantity, expiry_date) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {
            // Check if the batch already exists
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, id);
                checkStmt.setDate(2, Date.valueOf(expiryDate));
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Batch exists, so update quantity
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, id);
                        updateStmt.setDate(3, Date.valueOf(expiryDate));
                        updateStmt.executeUpdate();
                        System.out.println("STOCK: Updated quantity of existing batch " + id);
                    }
                } else {
                    // Batch doesn't exist, insert new
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, id);
                        insertStmt.setString(2, name);
                        insertStmt.setInt(3, quantity);
                        insertStmt.setDate(4, Date.valueOf(expiryDate));
                        insertStmt.executeUpdate();
                        System.out.println("STOCK: Added new batch " + id);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("STOCK ERROR: " + e.getMessage());
        }
    }

    public void sell(int id, int quantity) {
        String selectQuery = "SELECT quantity FROM product_batches WHERE id = ?";
        String deleteQuery = "DELETE FROM product_batches WHERE id = ?";
        String updateQuery = "UPDATE product_batches SET quantity = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {

            selectStmt.setInt(1, id);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                int availableQty = resultSet.getInt("quantity");

                if (availableQty < quantity) {
                    System.out.println("SELL ERROR: Only " + availableQty + " units available in batch " + id);
                } else if (availableQty == quantity) {
                    try (PreparedStatement delStmt = connection.prepareStatement(deleteQuery)) {
                        delStmt.setInt(1, id);
                        delStmt.executeUpdate();
                        System.out.println("SELL: Sold batch " + id);
                    }
                } else {
                    int newQty = availableQty - quantity;
                    quantity = 0;

                    try (PreparedStatement updStmt = connection.prepareStatement(updateQuery)) {
                        updStmt.setInt(1, newQty);
                        updStmt.setInt(2, id);
                        updStmt.executeUpdate();
                        System.out.println("SELL: Sold part of batch " + id);
                    }
                }
            }

            if (quantity > 0) {
                System.out.println("SELL: Not enough stock to sell remaining " + quantity + " units.");
            }

        } catch (SQLException e) {
            System.out.println("SELL ERROR: " + e.getMessage());
        }
    }

    public void update(int id, String newExpiryDate) {
        String query = "UPDATE product_batches SET expiry_date = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(newExpiryDate));
            stmt.setInt(2, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("UPDATE: Batch " + id + " updated.");
            } else {
                System.out.println("UPDATE: Batch " + id + " not found.");
            }
        } catch (SQLException e) {
            System.out.println("UPDATE ERROR: " + e.getMessage());
        }
    }

    public void expiryAlert(String date, int days) {
        String query = "SELECT * FROM product_batches WHERE expiry_date <= ?";
        LocalDate referenceDate = LocalDate.parse(date);
        LocalDate alertDate = referenceDate.plusDays(days);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(alertDate));
            ResultSet resultSet = stmt.executeQuery();

            System.out.println("EXPIRY ALERT: Products expiring by " + alertDate);
            boolean found = false;

            while (resultSet.next()) {
                found = true;
                System.out.println("Batch " + resultSet.getInt("id") + " (" + resultSet.getString("name") + ") expires on " + resultSet.getDate("expiry_date"));
            }

            if (!found) {
                System.out.println("No batches expiring within the given timeframe.");
            }

        } catch (SQLException e) {
            System.out.println("EXPIRY ALERT ERROR: " + e.getMessage());
        }
    }

    public void viewStock() {
        String query = "SELECT * FROM product_batches ORDER BY expiry_date ASC";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("AVAILABLE STOCK: ");
            while (resultSet.next()) {
                System.out.println("Batch ID: " + resultSet.getInt("id") +
                        ", Name: " + resultSet.getString("name") +
                        ", Quantity: " + resultSet.getInt("quantity") +
                        ", Expiry Date: " + resultSet.getDate("expiry_date"));
            }

        } catch (SQLException e) {
            System.out.println("VIEW STOCK ERROR: " + e.getMessage());
        }
    }

    public void runMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== INVENTORY MANAGEMENT MENU ===");
            System.out.println("1. Stock a new product batch");
            System.out.println("2. Sell products");
            System.out.println("3. Update expiry date of a batch");
            System.out.println("4. Check expiry alerts");
            System.out.println("5. View available stocks");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    while(true){
                        System.out.print("Enter batch ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();  // consume newline
                        System.out.print("Enter product name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter quantity: ");
                        int qty = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter expiry date (YYYY-MM-DD): ");
                        String exp = sc.nextLine();
                        stock(id, name, qty, exp);
                        System.out.print("Wants to add more batch(Y/N): ");
                        String str = sc.next();
                        if(str.toUpperCase().equals("N")){
                            break;
                        }
                    }
                    break;
                case 2:
                    System.out.print("Enter the product id: ");
                    int id = sc.nextInt();
                    System.out.print("Enter quantity to sell: ");
                    int q = sc.nextInt();
                    sell(q);
                    break;
                case 3:
                    System.out.print("Enter batch ID to update: ");
                    int updateId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter new expiry date (YYYY-MM-DD): ");
                    String newDate = sc.nextLine();
                    update(updateId, newDate);
                    break;
                case 4:
                    sc.nextLine();  // consume newline
                    System.out.print("Enter reference date (YYYY-MM-DD): ");
                    String date = sc.nextLine();
                    System.out.print("Enter number of days for expiry alert: ");
                    int days = sc.nextInt();
                    expiryAlert(date, days);
                    break;
                case 5:
                    viewStock();
                    break;
                case 6:
                    System.out.println("Exiting program...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public static void main(String[] args) {
        InventoryManagementSystem ims = new InventoryManagementSystem();
        ims.runMenu();
    }
}

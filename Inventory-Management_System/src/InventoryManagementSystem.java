import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class InventoryManagementSystem {

    public void stock(int id, String name, int quantity, String expiryDate) {
        String query = "INSERT INTO product_batches (id, name, quantity, expiry_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setInt(3, quantity);
            stmt.setDate(4, Date.valueOf(expiryDate));
            stmt.executeUpdate();

            System.out.println("STOCK: Added batch " + id);
        } catch (SQLException e) {
            System.out.println("STOCK ERROR: " + e.getMessage());
        }
    }

    public void sell(int quantity) {
        String selectQuery = "SELECT * FROM product_batches ORDER BY expiry_date ASC";
        String deleteQuery = "DELETE FROM product_batches WHERE id = ?";
        String updateQuery = "UPDATE product_batches SET quantity = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {

            while (quantity > 0 && rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int availableQty = rs.getInt("quantity");

                if (availableQty <= quantity) {
                    quantity -= availableQty;

                    try (PreparedStatement delStmt = conn.prepareStatement(deleteQuery)) {
                        delStmt.setInt(1, id);
                        delStmt.executeUpdate();
                        System.out.println("SELL: Sold batch " + id);
                    }
                } else {
                    int newQty = availableQty - quantity;
                    quantity = 0;

                    try (PreparedStatement updStmt = conn.prepareStatement(updateQuery)) {
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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(alertDate));
            ResultSet rs = stmt.executeQuery();

            System.out.println("EXPIRY ALERT: Products expiring by " + alertDate);
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("Batch " + rs.getInt("id") + " (" + rs.getString("name") + ") expires on " + rs.getDate("expiry_date"));
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

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("AVAILABLE STOCK:");
            while (rs.next()) {
                System.out.println("Batch ID: " + rs.getInt("id") +
                        ", Name: " + rs.getString("name") +
                        ", Quantity: " + rs.getInt("quantity") +
                        ", Expiry Date: " + rs.getDate("expiry_date"));
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
                    break;
                case 2:
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

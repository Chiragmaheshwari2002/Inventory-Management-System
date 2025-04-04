# Inventory-Management-System
This project is a console-based Inventory Management System developed using Java and integrated with a MySQL database. It allows administrators to efficiently manage stock, monitor product expiries, and perform sales using the FIFO (First-In-First-Out) strategy.

🔧 Features
✅ Stock Management: Add new product batches with ID, name, quantity, and expiry date.

🛒 Sales Processing: Sell products following FIFO logic to ensure older stock is used first.

✏️ Update Expiry Date: Modify expiry dates for specific batches as needed.

⏰ Expiry Alerts: Get alerts for products nearing expiry within a configurable time window.

📊 View Available Stocks: Display all current product batches with their quantities and expiry dates.

💾 MySQL Integration: All data is stored and managed in a MySQL database using JDBC.

🛠 Technologies Used
Java (JDK 17+)

MySQL

JDBC (Java Database Connectivity)

Object-Oriented Programming (OOP)

🚀 Getting Started
Clone this repository.

Set up the MySQL database:

Create a database named inventorydb

Run the SQL script to create the product_batches table

Update your MySQL credentials in the DBConnection.java file.

Compile and run InventoryManagementSystem.java.

📂 Future Enhancements
GUI using JavaFX or Swing

User authentication (admin roles)

Export reports to CSV/PDF

Web-based version with Spring Boot

👤 Author
Chirag Maheshwari
M.Sc. Cybersecurity & Digital Forensics
Cybersecurity enthusiast | Java Developer | Open Source Contributor


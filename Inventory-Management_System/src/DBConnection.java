import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/<DATABASENAME>";
    private static final String USER = "<USERNAME>"; // change if different
    private static final String PASSWORD = "<PASSWORD>"; // change if different

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());;
        }
    }
}

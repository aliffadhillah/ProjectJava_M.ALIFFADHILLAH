package helpers;

    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;


public class koneksi {
    public static Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting...");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/praktikumjavaalif",
                    "root",
                    "");
            System.out.println("Connected!");
        } catch (ClassNotFoundException e){
            System.out.println("Connection Error!");
        } catch (SQLException e){
            System.out.println("SQL Error!");
        }
        return connection;
    }
}

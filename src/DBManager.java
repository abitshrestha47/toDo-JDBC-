import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
public class DBManager {
    public Connection conn;
    DBManager() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    String url="jdbc:mysql://localhost/todo";
    conn=DriverManager.getConnection(url,"root","root");
//    System.out.println("connection succed");
    }
}

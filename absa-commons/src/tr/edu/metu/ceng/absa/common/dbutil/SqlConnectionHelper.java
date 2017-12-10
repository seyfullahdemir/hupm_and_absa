/**
 * 
 */
package tr.edu.metu.ceng.absa.common.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Batuhan
 *
 */
public class SqlConnectionHelper {

	private Connection conn = null;

	public SqlConnectionHelper(String address, String dbName, String user,
			String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection("jdbc:mysql://" + address + "/" + dbName + "?&characterEncoding=utf-8", user, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean executeUpdateQuery(String query) {
		boolean isSuccessful = true;
		
		try {
			Statement statement = conn.createStatement() ;
			int affectedRows = statement.executeUpdate(query);
			
			isSuccessful = affectedRows == 1;
		} catch (SQLException e) {
			isSuccessful = false;
			System.out.println("hata");
			System.out.println(query);
			e.printStackTrace();
		}
		
		return isSuccessful;
	}
	
	public ResultSet executeSelectQuery(String query) {
		// Statements allow to issue SQL queries to the database
		
		Statement statement = null;
		// Result set get the result of the SQL query
		ResultSet resultSet = null;
		
		try {
			statement = conn.createStatement();
			resultSet = statement.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}
}

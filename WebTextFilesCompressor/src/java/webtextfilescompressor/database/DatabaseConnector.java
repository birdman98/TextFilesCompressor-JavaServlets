package webtextfilescompressor.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Piotr Matras
 * @version 1.0
 */
public class DatabaseConnector {
    
    private final String databaseUsername = "dev";
    private final String databasePassword = "dev";
    private final String databaseURL = "jdbc:derby://localhost:1527/OperationsHistory";
    private final String tableName = "OperationsHistory";
    
    private static Connection connection = null;
    
    public DatabaseConnector() {
         if(connection == null) {
             connectWithDatabase();  
             createTable();
         }        
    }
    
    private boolean loadJDBCDriver() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver"); 
            return true;
        } catch(ClassNotFoundException e) {
            System.err.println("Failed to load JDBC driver, reason: " + e.getMessage());
        }
        
        return false;               
    }
    
    private void connectWithDatabase() {
        if(!loadJDBCDriver()) {
            System.err.println("Failed to load JDBC driver!");
            return;
        }
        
        try {
            connection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
        } catch(SQLException e) {
            System.err.println("Failed to get connection with database, reason: " + e.getMessage());
        }   
    }
    
    private void createTable() {
        try {
            Statement statement = connection.createStatement();   
            statement.executeUpdate("CREATE TABLE " + this.tableName +
                    " (mode VARCHAR(10), inputFile VARCHAR(255), outputFile VARCHAR(255))");
            System.out.println("Table created successfully!");
        } catch(SQLException e) {
            System.err.println("Failed to create table into database, reason: " + e.getMessage());
        }        
    }
    
    public boolean insertData(final String mode, final String inputFile, final String outputFile) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO " + this.tableName + " VALUES (?, ?, ?)");
            preparedStatement.setString(1, mode);
            preparedStatement.setString(2, inputFile);
            preparedStatement.setString(3, outputFile);
            preparedStatement.executeUpdate();
            
            System.out.println("Data inserted!");            
            return true;
        } catch(SQLException e) {
            System.err.println("Failed to insert data into database, reason: " + e.getMessage());
        }
        
        return false;
    }
    
    public List<List<String>> getOperationsHistory() {
        List<List<String>> operationsHistory = new ArrayList<List<String>>();
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM OperationsHistory");    
            
            while(resultSet.next()) {
                List<String> history = new ArrayList<>();
                history.add(resultSet.getString("mode"));
                history.add(resultSet.getString("inputFile"));
                history.add(resultSet.getString("outputFile"));
                
                operationsHistory.add(history);
            }
            resultSet.close();
            
        } catch(SQLException e) {
            System.err.println("Failed to get data from database, reason: " + e.getMessage());
        }
        
        return operationsHistory;        
    }
    
}

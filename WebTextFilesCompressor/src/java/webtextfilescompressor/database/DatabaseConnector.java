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
 * Class for realising connection with database and operations on database
 * @author Piotr Matras
 * @version 1.0
 */
public class DatabaseConnector {
    
    /**
     * databaseUsername - name of user to connect with database
     */
    private final String databaseUsername = "dev";
    /**
     * databasePassword - password of user to connect with database
     */
    private final String databasePassword = "dev";
    /**
     * databaseURL - URL to connect with database
     */
    private final String databaseURL = "jdbc:derby://localhost:1527/OperationsHistory";
    /**
     * tableName - name of table in database
     */
    private final String tableName = "OperationsHistory";
    
    /**
     * connection - object of Connection class, which represents connection with database
     */
    private static Connection connection = null;
    
    /**
     * Makes new connection with database if already not connected and tries to create table, if not exists
     */
    public DatabaseConnector() {
         if(connection == null) {
             connectWithDatabase();  
             createTable();
         }        
    }
    
    /**
     * Loads the JDBC driver
     * @return true if loading was successful, else false
     */
    private boolean loadJDBCDriver() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver"); 
            return true;
        } catch(ClassNotFoundException e) {
            System.err.println("Failed to load JDBC driver, reason: " + e.getMessage());
        }
        
        return false;               
    }
    
    /**
     * Makes connection with database if JDBC driver was loaded successfully
     */    
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
    
    /**
     * Creates table in database if already not exists
     */
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
    
    /**
     * Inserts data into table in database
     * @param mode - mode of file compressor
     * @param inputFile - input file path
     * @param outputFile - output file path
     * @return true if data inserted successfuly, else false
     */
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
    
    /**
     * Gets all records from table in database and parses it into collection of data
     * @return List of all records in table in database
     */
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

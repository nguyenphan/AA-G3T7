/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

/**
 *
 * @author ptlenguyen
 */
public class DatabaseConnectionString {
    private static DatabaseConnectionString instance = null;
    private int connectionStringIndex;
    
    private DatabaseConnectionString () {
        this.connectionStringIndex = 0;
    }
    
    public static synchronized DatabaseConnectionString getInstance(){
        if (instance == null){
            instance = new DatabaseConnectionString();
        }
        return instance;
    }
    
    public void switchConnectionString() {
        if (this.connectionStringIndex == 0) {
            this.connectionStringIndex = 1;
        } else {
            this.connectionStringIndex = 0;
        }
    }
    
    public String getConnectionString () {
        if (this.connectionStringIndex == 0) {
            return "jdbc:mysql://192.168.0.3:5000/aastockexchange_db?autoReconnect=true&connectTimeout=500&failOverReadOnly=false";
        } else {
            return "jdbc:mysql://192.168.0.4:5000/aastockexchange_db?autoReconnect=true&connectTimeout=500&failOverReadOnly=false";
        }
    }
}

package ebolo.libma.stub.utils.configs;

/**
 * Contains various of configurations about databases for central stub
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class DbConfigurations {
    private static final String
        userName = "ebolo",
        bookDbName = "books",
        userDbName = "users",
        transactionDbName = "transactions",
        mainDbName = "libmantest",
        password = "eboloyolo5653",
        dbHost = "localhost";
    
    public static String getUserName() {
        return userName;
    }
    
    public static String getBookDbName() {
        return bookDbName;
    }
    
    public static String getUserDbName() {
        return userDbName;
    }
    
    public static String getMainDbName() {
        return mainDbName;
    }
    
    public static String getTransactionDbName() {
        return transactionDbName;
    }
    
    public static String getPassword() {
        return password;
    }
    
    public static String getDbHost() {
        return dbHost;
    }
}

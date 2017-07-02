package ebolo.libma.stub.net.managers;

import com.sun.istack.internal.Nullable;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import org.bson.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class monitors the list of active users that has successfully authenticated to the system
 *
 * @author Ebolo
 * @version 07/06/2017
 * @since 06/06/2017
 */

public class ActiveUserManager {
    private static ActiveUserManager ourInstance;
    private Map<String, SocketWrapper> activeUsers;
    
    private ActiveUserManager() {
        activeUsers = new HashMap<>();
    }
    
    public static ActiveUserManager getInstance() {
        if (ourInstance == null)
            ourInstance = new ActiveUserManager();
        return ourInstance;
    }
    
    /**
     * This method is called to shutdown all the active connections
     */
    public void shutdown() {
        activeUsers.values().forEach(activeUser -> {
            try {
                activeUser.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        activeUsers.clear();
    }
    
    /**
     * This method is used for broadcasting out a message to all active users
     *
     * @param userId  message source
     * @param message message content in Bson document
     */
    public void sendMessageToAll(@Nullable String userId, Document message) {
        activeUsers.forEach((s, socketWrapper) -> {
            if (userId == null || !s.equals(userId))
                socketWrapper.sendMessage(message);
        });
    }
    
    public void sendMessageToAllStudents(@Nullable String userId, Document message) {
        activeUsers.forEach((s, socketWrapper) -> {
            if ((userId == null || !s.equals(userId)) && socketWrapper.getUserMode() == MetaInfo.USER_MODE.Student)
                socketWrapper.sendMessage(message);
        });
    }
    
    public void sendMessageToAllLibrarians(@Nullable String userId, Document message) {
        activeUsers.forEach((s, socketWrapper) -> {
            if ((userId == null || !s.equals(userId)) && socketWrapper.getUserMode() == MetaInfo.USER_MODE.Librarian)
                socketWrapper.sendMessage(message);
        });
    }
    
    /**
     * Register an authenticated user to the system
     *
     * @param userId     user object id
     * @param userSocket user's connection
     * @throws IOException is thrown if there was any internal error
     */
    public void registerUser(String userId, SocketWrapper userSocket) throws IOException {
        activeUsers.put(userId, userSocket);
        Logger.getLogger("myApp").log(Level.INFO, "A user has just joined");
        Logger.getLogger("myApp").log(Level.INFO, "Active user: " + activeUsers.size());
    }
    
    /**
     * De-register an authenticated user from the system
     *
     * @param userId user object id
     * @throws IOException is thrown if there was any internal error
     */
    public void deregisterUser(String userId) throws IOException {
        activeUsers.get(userId).close();
        activeUsers.remove(userId);
        Logger.getLogger("myApp").log(Level.INFO, "A user has just left");
        Logger.getLogger("myApp").log(Level.INFO, "Active user: " + activeUsers.size());
    }
}

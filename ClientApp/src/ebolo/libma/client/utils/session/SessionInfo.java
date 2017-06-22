package ebolo.libma.client.utils.session;

/**
 * Class contains session info for client application
 *
 * @author Ebolo
 * @version 07/06/2017
 * @since 07/06/2017
 */

public class SessionInfo {
    private static SessionInfo ourInstance;
    
    private String studentObjId;
    
    private SessionInfo() {
    }
    
    public static SessionInfo getInstance() {
        if (ourInstance == null)
            ourInstance = new SessionInfo();
        return ourInstance;
    }
    
    public String getStudentObjId() {
        return studentObjId;
    }
    
    public void setStudentObjId(String studentObjId) {
        this.studentObjId = studentObjId;
    }
    
    public void closeSession() {
        // TODO: add implementation
    }
}

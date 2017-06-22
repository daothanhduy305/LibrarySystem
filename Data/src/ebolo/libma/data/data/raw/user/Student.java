package ebolo.libma.data.data.raw.user;

import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import org.bson.Document;

/**
 * This is the demonstration class of the internal database student entity
 * Student basically is a user thus contains user's entities plus extra student's info
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see User
 * @since 06/06/2017
 */

public class Student extends User {
    
    private MetaInfo.STUDENT_COURSE studentCourse;
    /**
     * indicates which year that the student take the major (course)
     */
    private int intake;
    /**
     * totally active borrowing transaction that has not been returned
     */
    private int borrowing;
    /**
     * real life student ID
     */
    private String studentId;
    
    public Student(String firstName, String middleName, String lastName, String email,
                   String userName, String password, MetaInfo.STUDENT_COURSE studentCourse, int intake, String studentId) {
        super(MetaInfo.USER_MODE.Student, firstName, middleName, lastName, email, userName, password);
        
        this.studentCourse = studentCourse;
        this.intake = intake;
        this.studentId = studentId;
        this.borrowing = 0;
    }
    
    public Student(Document document) {
        super(document);
        this.studentCourse = MetaInfo.STUDENT_COURSE.valueOf(document.getString("student_course"));
        this.studentId = document.getString("student_id");
        this.intake = document.getInteger("student_intake");
        this.borrowing = document.getInteger("borrowing");
    }
    
    public MetaInfo.STUDENT_COURSE getStudentCourse() {
        return studentCourse;
    }
    
    public int getIntake() {
        return intake;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public int getBorrowing() {
        return borrowing;
    }
    
    @Override
    public Document toMongoDocument() {
        prepareBasicUserDocument();
        userDocument.put("student_id", studentId);
        userDocument.put("student_course", studentCourse.name());
        userDocument.put("student_intake", intake);
        userDocument.put("borrowing", borrowing);
        return userDocument;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        
        Student student = (Student) o;
        
        return intake == student.intake &&
            borrowing == student.borrowing &&
            studentCourse == student.studentCourse &&
            studentId.equals(student.studentId);
    }
    
    @Override
    public int hashCode() {
        int result = studentCourse.hashCode();
        result = 31 * result + intake;
        result = 31 * result + borrowing;
        result = 31 * result + studentId.hashCode();
        return result;
    }
}

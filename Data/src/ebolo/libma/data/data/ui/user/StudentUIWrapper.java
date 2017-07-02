package ebolo.libma.data.data.ui.user;

import ebolo.libma.data.data.raw.user.Student;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A UI wrapper class for raw <code>Student</code> data type. All of its fields are properties that can be bound to JavaFX
 * UI components, and have the value from the host object.
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see Student
 * @see UserUIWrapper
 * @since 12/06/2017
 */

public class StudentUIWrapper extends UserUIWrapper<Student> {
    private StringProperty course, studentId;
    private IntegerProperty intake, borrowing;
    
    public StudentUIWrapper(Student student, String objectId) {
        super(student, objectId);
        
        this.course = new SimpleStringProperty(
            student.getStudentCourse().name().replace('_', ' '));
        this.studentId = new SimpleStringProperty(student.getStudentId());
        this.intake = new SimpleIntegerProperty(student.getIntake());
        this.borrowing = new SimpleIntegerProperty(student.getBorrowing());
    }
    
    public StringProperty courseProperty() {
        return course;
    }
    
    public StringProperty studentIdProperty() {
        return studentId;
    }
    
    public IntegerProperty borrowingProperty() {
        return borrowing;
    }
    
    public IntegerProperty intakeProperty() {
        return intake;
    }
    
    @Override
    public void update(Student student) {
        super.update(student);
        
        this.course.set(student.getStudentCourse().name().replace('_', ' '));
        this.studentId.set(student.getStudentId());
        this.intake.set(student.getIntake());
        this.borrowing.set(student.getBorrowing());
    }
}

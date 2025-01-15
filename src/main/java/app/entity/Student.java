package app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Entity
public class Student {

    @Id
    @Column(name = "sid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // id 자동 할당
    private long studentId;

    @Column(name = "sname", nullable = false)
    private String studentName;

    public void updateSname(Student student) { // 학생 이름 변경
        this.studentName = student.getStudentName();
    }
}

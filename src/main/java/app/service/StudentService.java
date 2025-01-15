package app.service;

import app.entity.Student;
import app.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;

    public List<Student> getAllStudents() { // DB 에 있는 모든 학생 목록 반환
        return studentRepository.findAll();
    }

    public Student getStudentById(long id) { // id 기준 해당 학생 정보 반환
        return studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 학생이 존재하지 않습니다."));
    }

    public Student registerStudent(Student student) { // 새로운 학생 저장
        return studentRepository.save(student);
    }

    public Student updateStudent(long id, Student student) { // 학생 정보 변경
        Student st = studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 학생이 존재하지 않습니다."));
        if (st == null) {
            return null;
        }

        st.updateSname(student);

        return studentRepository.save(st);
    }

    public Student deleteDeptById(long id) { // 해당 id 의 학생 정보 삭제
        Student student = studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 학생이 존재하지 않습니다."));
        studentRepository.delete(student);

        return student; // 삭제한 학생 정보 반환
    }




}

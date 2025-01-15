package app.controller;

import app.entity.Student;
import app.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("")
    public List<Student> getStudents() { // /api/students 로 오는 Get 요청 처리 => 모든 학생 정보 반환
        List<Student> students = studentService.getAllStudents();

        return students;
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable long id) { // /api/students/{id} 로 오는 Get 요청 처리 => id 에 해당하는 학생 정보 반환
        Student student = studentService.getStudentById(id);

        return student;
    }

    @PostMapping("")
    public Student registerStudent(@RequestBody Student student) { // /api/students 로 오는 post 요청 처리 => 새로운 학생 정보 저장
        Student newStudent = studentService.registerStudent(student);

        return newStudent;

    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable long id, @RequestBody Student updateSt) { // /api/students/{id} 로 오는 put 요청 처리 => id 에 해당하는 학생 정보 변경
        Student st = studentService.updateStudent(id, updateSt);

        return st;
    }

    @DeleteMapping("/{id}")
    public Student deleteStudent(@PathVariable long id) {
        Student delSt = studentService.deleteDeptById(id);

        return delSt;
    }




}

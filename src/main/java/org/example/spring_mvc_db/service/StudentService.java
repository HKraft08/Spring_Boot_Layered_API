package org.example.spring_mvc_db.service;

import org.springframework.transaction.annotation.Transactional;
import org.example.spring_mvc_db.DTO.StudentRequest;
import org.example.spring_mvc_db.DTO.StudentResponse;
import org.example.spring_mvc_db.entity.Students;
import org.example.spring_mvc_db.exceptions.DuplicateResourceException;
import org.example.spring_mvc_db.exceptions.ResourceNotFoundException;
import org.example.spring_mvc_db.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    //CREATE
    @Transactional
    public StudentResponse createStudent (StudentRequest request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("A student with this email already exists");
        }

        Students student = new Students();
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());

        Students savedStudent = studentRepository.save(student);

        return mapToResponse(savedStudent);
    }

    //READ ALL
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {

        return studentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //READ ONE
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {

        Students student = findStudentByIdOrThrow(id);
        return mapToResponse(student);
    }

    //READ ONE BY EMAIL
    @Transactional(readOnly = true)
    public StudentResponse getStudentByEmail(String email) {

        Students student = findStudentByEmailOrThrow(email);
        return mapToResponse(student);
    }

    //UPDATE
    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {

        Students student = findStudentByIdOrThrow(id);

        boolean emailUsedByAnotherStudent = studentRepository.existsByEmailAndIdNot(
                request.email(),
                id
        );

        if (emailUsedByAnotherStudent) {
            throw new DuplicateResourceException("Another student is already using this email");
        }

        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());

        Students updatedStudent = studentRepository.save(student);

        return mapToResponse(updatedStudent);
    }

    //DELETE
    @Transactional
    public void deleteStudent(Long id) {

        Students student = findStudentByIdOrThrow(id);

        studentRepository.delete(student);
    }

    private Students findStudentByIdOrThrow(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    private Students findStudentByEmailOrThrow(String email) {

        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }

    private StudentResponse mapToResponse(Students student) {

        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()
        );
    }

}

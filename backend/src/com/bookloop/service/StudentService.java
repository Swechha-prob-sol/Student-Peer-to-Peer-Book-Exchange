package com.bookloop.service;

import com.bookloop.data.DataStore;
import com.bookloop.model.Student;
import com.bookloop.model.User;
import com.bookloop.util.ValidationUtil;
import java.util.List;

/**
 * Service handling student registration, authentication, and profile lookups.
 * Demonstrates business logic separation and input validation using ValidationUtil.
 */
public class StudentService {
    private final DataStore store;
    private static int nextStudentIdNum = 110;

    public StudentService(DataStore store) {
        this.store = store;
    }

    /**
     * Registers a new student in the system.
     * Demonstrates validation and encapsulation rules via ValidationUtil.
     */
    public Student registerStudent(String name, String email, String phone,
                                   String college, String department, int semester) {
        // Enforce validation rules using reusable ValidationUtil
        ValidationUtil.validateStudent(name, email, phone, college, department, semester);

        if (store.isEmailRegistered(email)) {
            throw new IllegalArgumentException("A student is already registered with this email: " + email);
        }

        String studentId = "STU" + (nextStudentIdNum++);
        String today = java.time.LocalDate.now().toString();

        Student student = new Student(studentId, name, email, phone, today, college, department, semester);
        boolean success = store.addStudent(student);
        return success ? student : null;
    }

    public Student getStudentById(String id) {
        return store.getStudent(id);
    }

    public Student getStudentByEmail(String email) {
        return store.getStudentByEmail(email);
    }

    public List<Student> getAllStudents() {
        return store.getAllStudents();
    }
}

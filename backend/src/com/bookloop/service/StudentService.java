package com.bookloop.service;

import com.bookloop.data.DataStore;
import com.bookloop.model.Student;
import com.bookloop.model.User;
import java.util.List;

/**
 * Service handling student registration, authentication, and profile lookups.
 * Demonstrates business logic separation and input validation.
 */
public class StudentService {
    private final DataStore store;
    private static int nextStudentIdNum = 110;

    public StudentService(DataStore store) {
        this.store = store;
    }

    /**
     * Registers a new student in the system.
     * Demonstrates validation and encapsulation rules.
     */
    public Student registerStudent(String name, String email, String phone,
                                   String college, String department, int semester) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
        if (!User.isValidCollegeEmail(email)) {
            throw new IllegalArgumentException("Invalid college email format. Example: name@college.ac.in");
        }
        if (store.isEmailRegistered(email)) {
            throw new IllegalArgumentException("A student is already registered with this email: " + email);
        }
        if (semester < 1 || semester > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8.");
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

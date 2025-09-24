package ccrm.service;

import ccrm.domain.Student;
import ccrm.domain.Enrollment;
import ccrm.domain.Course;
import ccrm.domain.Grade;
import ccrm.util.ValidationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StudentService implements Searchable<Student> {
    private List<Student> students;
    private static final int MAX_CREDITS_PER_SEMESTER = 18;
    
    public StudentService() {
        this.students = new ArrayList<>();
    }
    
    public void addStudent(Student student) {
        ValidationUtils.validateNotNull(student, "Student cannot be null");
        
        // Check if student with same ID already exists
        if (findById(student.getId()).isPresent()) {
            throw new IllegalArgumentException("Student with ID " + student.getId() + " already exists");
        }
        
        students.add(student);
    }
    
    public void updateStudent(String id, String fullName, String email) {
        ValidationUtils.validateNotNull(id, "Student ID cannot be null");
        
        Student student = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Student with ID " + id + " not found"));
        
        if (fullName != null && !fullName.trim().isEmpty()) {
            student.setFullName(fullName);
        }
        
        if (email != null && !email.trim().isEmpty()) {
            if (ValidationUtils.isValidEmail(email)) {
                student.setEmail(email);
            } else {
                throw new IllegalArgumentException("Invalid email format");
            }
        }
    }
    
    public void deactivateStudent(String id) {
        Student student = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Student with ID " + id + " not found"));
        student.setActive(false);
    }
    
    public void enrollInCourse(String studentId, Course course) {
        ValidationUtils.validateNotNull(studentId, "Student ID cannot be null");
        ValidationUtils.validateNotNull(course, "Course cannot be null");
        
        Student student = findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student with ID " + studentId + " not found"));
        
        // Check if student is already enrolled in this course
        boolean alreadyEnrolled = student.getEnrollments().stream()
            .anyMatch(e -> e.getCourse().equals(course));
        
        if (alreadyEnrolled) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }
        
        // Check credit limit
        int currentCredits = student.getEnrollments().stream()
            .mapToInt(e -> e.getCourse().getCredits())
            .sum();
        
        if (currentCredits + course.getCredits() > MAX_CREDITS_PER_SEMESTER) {
            throw new IllegalStateException("Credit limit exceeded. Maximum allowed: " + MAX_CREDITS_PER_SEMESTER);
        }
        
        Enrollment enrollment = new Enrollment(student, course);
        student.addEnrollment(enrollment);
    }
    
    public void recordGrade(String studentId, String courseCode, Grade grade) {
        ValidationUtils.validateNotNull(studentId, "Student ID cannot be null");
        ValidationUtils.validateNotNull(courseCode, "Course code cannot be null");
        ValidationUtils.validateNotNull(grade, "Grade cannot be null");
        
        Student student = findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student with ID " + studentId + " not found"));
        
        Optional<Enrollment> enrollment = student.getEnrollments().stream()
            .filter(e -> e.getCourse().getCode().equals(courseCode))
            .findFirst();
        
        if (enrollment.isPresent()) {
            enrollment.get().setGrade(grade);
        } else {
            throw new IllegalArgumentException("Student is not enrolled in course " + courseCode);
        }
    }
    
    public String generateTranscript(String studentId) {
        Student student = findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student with ID " + studentId + " not found"));
        
        StringBuilder transcript = new StringBuilder();
        transcript.append("Transcript for: ").append(student.getFullName()).append("\n");
        transcript.append("Registration No: ").append(student.getRegNo()).append("\n");
        transcript.append("GPA: ").append(String.format("%.2f", student.calculateGPA())).append("\n\n");
        transcript.append("Courses:\n");
        
        student.getEnrollments().forEach(enrollment -> {
            transcript.append(String.format("- %s: %s (%d credits) - %s\n",
                enrollment.getCourse().getCode(),
                enrollment.getCourse().getTitle(),
                enrollment.getCourse().getCredits(),
                enrollment.getGrade() != null ? enrollment.getGrade() : "Not graded"));
        });
        
        return transcript.toString();
    }
    
    @Override
    public List<Student> search(Predicate<Student> predicate) {
        return students.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students);
    }
    
    @Override
    public Optional<Student> findById(String id) {
        return students.stream()
            .filter(s -> s.getId().equals(id))
            .findFirst();
    }
    
    public Optional<Student> findByRegNo(String regNo) {
        return students.stream()
            .filter(s -> s.getRegNo().equals(regNo))
            .findFirst();
    }
}
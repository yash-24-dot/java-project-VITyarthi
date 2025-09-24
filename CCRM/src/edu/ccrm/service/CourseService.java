package ccrm.service;

import ccrm.domain.Course;
import ccrm.domain.Semester;
import ccrm.util.ValidationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CourseService implements Searchable<Course> {
    private List<Course> courses;
    
    public CourseService() {
        this.courses = new ArrayList<>();
    }
    
    public void addCourse(Course course) {
        ValidationUtils.validateNotNull(course, "Course cannot be null");
        
        // Check if course with same code already exists
        if (findById(course.getCode()).isPresent()) {
            throw new IllegalArgumentException("Course with code " + course.getCode() + " already exists");
        }
        
        courses.add(course);
    }
    
    public void updateCourse(String code, String title, Integer credits, String instructor, Semester semester, String department) {
        ValidationUtils.validateNotNull(code, "Course code cannot be null");
        
        Course course = findById(code)
            .orElseThrow(() -> new IllegalArgumentException("Course with code " + code + " not found"));
        
        if (title != null && !title.trim().isEmpty()) {
            course.setTitle(title);
        }
        
        if (credits != null && credits > 0) {
            course.setCredits(credits);
        }
        
        if (instructor != null) {
            course.setInstructor(instructor);
        }
        
        if (semester != null) {
            course.setSemester(semester);
        }
        
        if (department != null && !department.trim().isEmpty()) {
            course.setDepartment(department);
        }
    }
    
    public void deactivateCourse(String code) {
        Course course = findById(code)
            .orElseThrow(() -> new IllegalArgumentException("Course with code " + code + " not found"));
        course.setActive(false);
    }
    
    public List<Course> findByInstructor(String instructor) {
        return search(c -> c.getInstructor() != null && c.getInstructor().equalsIgnoreCase(instructor));
    }
    
    public List<Course> findByDepartment(String department) {
        return search(c -> c.getDepartment() != null && c.getDepartment().equalsIgnoreCase(department));
    }
    
    public List<Course> findBySemester(Semester semester) {
        return search(c -> c.getSemester() == semester);
    }
    
    @Override
    public List<Course> search(Predicate<Course> predicate) {
        return courses.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findAll() {
        return new ArrayList<>(courses);
    }
    
    @Override
    public Optional<Course> findById(String code) {
        return courses.stream()
            .filter(c -> c.getCode().equals(code))
            .findFirst();
    }
}
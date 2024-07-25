import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Course1 {
    private String courseCode;
    private String title;
    private String description;
    private int capacity;
    private String schedule;

    public Course1(String courseCode, String title, String description, int capacity, String schedule) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.capacity = capacity;
        this.schedule = schedule;
    }

    public String getCourseCode() {
        return courseCode;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public int getCapacity() {
        return capacity;
    }
    public String getSchedule() {
        return schedule;
    }
    @Override
    public String toString() {
        return title + " (" + courseCode + ")";
    }
}
class Student1 {
    private String studentID;
    private String name;

    public Student1(String studentID, String name) {
        this.studentID = studentID;
        this.name = name;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " (" + studentID + ")";
    }
}

class CourseRegistrationSystem1 {
    private Connection connection;

    public CourseRegistrationSystem1() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/suraj", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCourse(Course1 course) throws SQLException {
        String sql = "INSERT INTO courses (course_code, title, description, capacity, schedule) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getTitle());
            stmt.setString(3, course.getDescription());
            stmt.setInt(4, course.getCapacity());
            stmt.setString(5, course.getSchedule());
            stmt.executeUpdate();
        }
    }

    public void addStudent(Student1 student) throws SQLException {
        String sql = "INSERT INTO students (student_id, name) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
            stmt.setString(2, student.getName());
            stmt.executeUpdate();
        }
    }

    public List<Course1> getCourses() throws SQLException {
        List<Course1> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Course1 course = new Course1(
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("capacity"),
                        rs.getString("schedule")
                );
                courses.add(course);
            }
        }
        return courses;
    }

    public List<Student1> getStudents() throws SQLException {
        List<Student1> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Student1 student = new Student1(
                        rs.getString("student_id"),
                        rs.getString("name")
                );
                students.add(student);
            }
        }
        return students;
    }

    public Course1 findCourseByCode(String courseCode) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Course1(
                            rs.getString("course_code"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("capacity"),
                            rs.getString("schedule")
                    );
                }
            }
        }
        return null;
    }

    public Student1 findStudentByID(String studentID) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student1(
                            rs.getString("student_id"),
                            rs.getString("name")
                    );
                }
            }
        }
        return null;
    }

    public boolean studentExists(String studentID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE student_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean registerCourse(String studentID, String courseCode) throws SQLException {
        if (!studentExists(studentID)) {
            throw new SQLException("Student ID does not exist: " + studentID);
        }

        String sql = "INSERT INTO registrations (student_id, course_code) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentID);
            stmt.setString(2, courseCode);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                updateEnrolledStudents(courseCode, true);
                return true;
            }
        }
        return false;
    }

    public boolean dropCourse(String studentID, String courseCode) throws SQLException {
        String sql = "DELETE FROM registrations WHERE student_id = ? AND course_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentID);
            stmt.setString(2, courseCode);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                updateEnrolledStudents(courseCode, false);
                return true;
            }
        }
        return false;
    }

    private void updateEnrolledStudents(String courseCode, boolean increment) throws SQLException {
        String sql = "UPDATE courses SET enrolled_students = enrolled_students + ? WHERE course_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, increment ? 1 : -1);
            stmt.setString(2, courseCode);
            stmt.executeUpdate();
        }
    }
}

public class ProjectMain3 {
    private static CourseRegistrationSystem1 system;

    public static void main(String[] args) {
        system = new CourseRegistrationSystem1();
        SwingUtilities.invokeLater(ProjectMain3::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Course Registration System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel listCoursesPanel = new JPanel(new BorderLayout());
        DefaultListModel<Course1> courseListModel = new DefaultListModel<>();
        JList<Course1> courseList = new JList<>(courseListModel);
        listCoursesPanel.add(new JScrollPane(courseList), BorderLayout.CENTER);
        tabbedPane.addTab("List Courses", listCoursesPanel);

        JPanel registerPanel = new JPanel(new GridLayout(3, 2));
        JTextField studentIDField = new JTextField();
        JTextField courseCodeField = new JTextField();
        JButton registerButton = new JButton("Register");
        registerPanel.add(new JLabel("Student ID:"));
        registerPanel.add(studentIDField);
        registerPanel.add(new JLabel("Course Code:"));
        registerPanel.add(courseCodeField);
        registerPanel.add(registerButton);
        tabbedPane.addTab("Register for Course", registerPanel);

        JPanel dropPanel = new JPanel(new GridLayout(3, 2));
        JTextField dropStudentIDField = new JTextField();
        JTextField dropCourseCodeField = new JTextField();
        JButton dropButton = new JButton("Drop");
        dropPanel.add(new JLabel("Student ID:"));
        dropPanel.add(dropStudentIDField);
        dropPanel.add(new JLabel("Course Code:"));
        dropPanel.add(dropCourseCodeField);
        dropPanel.add(dropButton);
        tabbedPane.addTab("Drop Course", dropPanel);

        // Panel to add a new course
        JPanel addCoursePanel = new JPanel(new GridLayout(6, 2));
        JTextField courseCodeFieldAdd = new JTextField();
        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField capacityField = new JTextField();
        JTextField scheduleField = new JTextField();
        JButton addCourseButton = new JButton("Add Course");
        addCoursePanel.add(new JLabel("Course Code:"));
        addCoursePanel.add(courseCodeFieldAdd);
        addCoursePanel.add(new JLabel("Title:"));
        addCoursePanel.add(titleField);
        addCoursePanel.add(new JLabel("Description:"));
        addCoursePanel.add(descriptionField);
        addCoursePanel.add(new JLabel("Capacity:"));
        addCoursePanel.add(capacityField);
        addCoursePanel.add(new JLabel("Schedule:"));
        addCoursePanel.add(scheduleField);
        addCoursePanel.add(new JLabel(""));
        addCoursePanel.add(addCourseButton);
        tabbedPane.addTab("Add Course", addCoursePanel);

        // Panel to add a new student
        JPanel addStudentPanel = new JPanel(new GridLayout(3, 2));
        JTextField studentIDFieldAdd = new JTextField();
        JTextField studentNameField = new JTextField();
        JButton addStudentButton = new JButton("Add Student");
        addStudentPanel.add(new JLabel("Student ID:"));
        addStudentPanel.add(studentIDFieldAdd);
        addStudentPanel.add(new JLabel("Name:"));
        addStudentPanel.add(studentNameField);
        addStudentPanel.add(new JLabel(""));
        addStudentPanel.add(addStudentButton);
        tabbedPane.addTab("Add Student", addStudentPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);

        // add courses and student here
       /* try {

           // system.addCourse(new Course1("MATH101", "Calculus I", "Introduction to Calculus", 40, "TTh 9-10:30"));
           // system.addCourse(new Course1("ENG101", "English Literature", "Study of English literature", 20, "MWF 11-12"));
           // system.addStudent(new Student1("S001", "John Doe"));
           // system.addStudent(new Student1("S002", "Jane Smith"));
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        // Load courses into the list
        try {
            List<Course1> courses = system.getCourses();
            for (Course1 course : courses) {
                courseListModel.addElement(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = studentIDField.getText();
                String courseCode = courseCodeField.getText();
                try {
                    if (system.registerCourse(studentID, courseCode)) {
                        JOptionPane.showMessageDialog(frame, "Course registered successfully.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Course registration failed.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        dropButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = dropStudentIDField.getText();
                String courseCode = dropCourseCodeField.getText();
                try {
                    if (system.dropCourse(studentID, courseCode)) {
                        JOptionPane.showMessageDialog(frame, "Course dropped successfully.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Course drop failed.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        addCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseCode = courseCodeFieldAdd.getText();
                String title = titleField.getText();
                String description = descriptionField.getText();
                int capacity = Integer.parseInt(capacityField.getText());
                String schedule = scheduleField.getText();
                try {
                    system.addCourse(new Course1(courseCode, title, description, capacity, schedule));
                    courseListModel.addElement(new Course1(courseCode, title, description, capacity, schedule));
                    JOptionPane.showMessageDialog(frame, "Course added successfully.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        addStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = studentIDFieldAdd.getText();
                String name = studentNameField.getText();
                try {
                    system.addStudent(new Student1(studentID, name));
                    JOptionPane.showMessageDialog(frame, "Student added successfully.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });
    }
}
 class Project4 {
    private static CourseRegistrationSystem1 system;

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Course Registration System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel listCoursesPanel = new JPanel(new BorderLayout());
        DefaultListModel<Course1> courseListModel = new DefaultListModel<>();
        JList<Course1> courseList = new JList<>(courseListModel);
        listCoursesPanel.add(new JScrollPane(courseList), BorderLayout.CENTER);
        tabbedPane.addTab("List Courses", listCoursesPanel);

        JPanel registerPanel = new JPanel(new GridLayout(3, 2));
        JTextField studentIDField = new JTextField();
        JTextField courseCodeField = new JTextField();
        JButton registerButton = new JButton("Register");
        registerPanel.add(new JLabel("Student ID:"));
        registerPanel.add(studentIDField);
        registerPanel.add(new JLabel("Course Code:"));
        registerPanel.add(courseCodeField);
        registerPanel.add(registerButton);
        tabbedPane.addTab("Register for Course", registerPanel);

        JPanel dropPanel = new JPanel(new GridLayout(3, 2));
        JTextField dropStudentIDField = new JTextField();
        JTextField dropCourseCodeField = new JTextField();
        JButton dropButton = new JButton("Drop");
        dropPanel.add(new JLabel("Student ID:"));
        dropPanel.add(dropStudentIDField);
        dropPanel.add(new JLabel("Course Code:"));
        dropPanel.add(dropCourseCodeField);
        dropPanel.add(dropButton);
        tabbedPane.addTab("Drop Course", dropPanel);

        // Panel to add a new course
        JPanel addCoursePanel = new JPanel(new GridLayout(6, 2));
        JTextField courseCodeFieldAdd = new JTextField();
        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField capacityField = new JTextField();
        JTextField scheduleField = new JTextField();
        JButton addCourseButton = new JButton("Add Course");
        addCoursePanel.add(new JLabel("Course Code:"));
        addCoursePanel.add(courseCodeFieldAdd);
        addCoursePanel.add(new JLabel("Title:"));
        addCoursePanel.add(titleField);
        addCoursePanel.add(new JLabel("Description:"));
        addCoursePanel.add(descriptionField);
        addCoursePanel.add(new JLabel("Capacity:"));
        addCoursePanel.add(capacityField);
        addCoursePanel.add(new JLabel("Schedule:"));
        addCoursePanel.add(scheduleField);
        addCoursePanel.add(new JLabel(""));
        addCoursePanel.add(addCourseButton);
        tabbedPane.addTab("Add Course", addCoursePanel);

        // Panel to add a new student
        JPanel addStudentPanel = new JPanel(new GridLayout(3, 2));
        JTextField studentIDFieldAdd = new JTextField();
        JTextField studentNameField = new JTextField();
        JButton addStudentButton = new JButton("Add Student");
        addStudentPanel.add(new JLabel("Student ID:"));
        addStudentPanel.add(studentIDFieldAdd);
        addStudentPanel.add(new JLabel("Name:"));
        addStudentPanel.add(studentNameField);
        addStudentPanel.add(new JLabel(""));
        addStudentPanel.add(addStudentButton);
        tabbedPane.addTab("Add Student", addStudentPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);

        // add courses and student here
       /* try {

           // system.addCourse(new Course1("MATH101", "Calculus I", "Introduction to Calculus", 40, "TTh 9-10:30"));
           // system.addCourse(new Course1("ENG101", "English Literature", "Study of English literature", 20, "MWF 11-12"));
           // system.addStudent(new Student1("S001", "John Doe"));
           // system.addStudent(new Student1("S002", "Jane Smith"));
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        // Load courses into the list
        try {
            List<Course1> courses = system.getCourses();
            for (Course1 course : courses) {
                courseListModel.addElement(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = studentIDField.getText();
                String courseCode = courseCodeField.getText();
                try {
                    if (system.registerCourse(studentID, courseCode)) {
                        JOptionPane.showMessageDialog(frame, "Course registered successfully.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Course registration failed.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        dropButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = dropStudentIDField.getText();
                String courseCode = dropCourseCodeField.getText();
                try {
                    if (system.dropCourse(studentID, courseCode)) {
                        JOptionPane.showMessageDialog(frame, "Course dropped successfully.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Course drop failed.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        addCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseCode = courseCodeFieldAdd.getText();
                String title = titleField.getText();
                String description = descriptionField.getText();
                int capacity = Integer.parseInt(capacityField.getText());
                String schedule = scheduleField.getText();
                try {
                    system.addCourse(new Course1(courseCode, title, description, capacity, schedule));
                    courseListModel.addElement(new Course1(courseCode, title, description, capacity, schedule));
                    JOptionPane.showMessageDialog(frame, "Course added successfully.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        addStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = studentIDFieldAdd.getText();
                String name = studentNameField.getText();
                try {
                    system.addStudent(new Student1(studentID, name));
                    JOptionPane.showMessageDialog(frame, "Student added successfully.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });
    }
}

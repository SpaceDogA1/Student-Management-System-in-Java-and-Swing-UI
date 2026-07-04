import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * StudentManagementSystem
 *
 * A Swing-based GUI application for managing student records,
 * course enrollment, and grade assignment.
 *
 * Author: Austine
 * Course: CS 1102 - Programming 1 (Unit 7 Assignment)
 */
public class StudentManagementSystem extends JFrame {

    // ---------------------------------------------------------------
    // Data models
    // ---------------------------------------------------------------

    /** Represents one student record. */
    static class Student {
        String id;
        String name;
        String email;
        // courseId -> grade (null = enrolled but no grade yet)
        Map<String, String> enrollment = new LinkedHashMap<>();

        Student(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }

    /** Available courses offered in the system. */
    static final String[] COURSES = {
        "CS1101 - Intro to Computer Science",
        "CS1102 - Programming 1",
        "MATH1201 - College Algebra",
        "MATH1280 - Intro to Statistics",
        "ENGL1102 - English Composition"
    };

    /** Valid grade values shown in the grade assignment combo box. */
    static final String[] GRADES = {"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F"};

    // ---------------------------------------------------------------
    // Application state
    // ---------------------------------------------------------------

    private final Map<String, Student> students = new LinkedHashMap<>();
    private int nextIdSuffix = 1;

    // ---------------------------------------------------------------
    // Shared UI references
    // ---------------------------------------------------------------

    private JTabbedPane tabbedPane;

    // --- Student Management tab ---
    private DefaultTableModel studentTableModel;
    private JTable studentTable;

    // --- Enrollment tab ---
    private JComboBox<String> enrollCourseCombo;
    private JComboBox<String> enrollStudentCombo;
    private JLabel enrollStatusLabel;

    // --- Grade Management tab ---
    private JComboBox<String> gradeStudentCombo;
    private JComboBox<String> gradeCoursCombo;
    private JComboBox<String> gradeValueCombo;
    private DefaultTableModel gradeTableModel;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    public StudentManagementSystem() {
        setTitle("Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);

        // Seed sample data so the UI is not empty on first launch
        seedSampleData();

        initUI();
        refreshAllViews();
    }

    // ---------------------------------------------------------------
    // Sample data
    // ---------------------------------------------------------------

    private void seedSampleData() {
        addStudentInternal("Alice Njeri", "alice@email.com");
        addStudentInternal("Brian Otieno", "brian@email.com");
        students.values().iterator().next()
            .enrollment.put(COURSES[0], null);
    }

    /**
     * Core method that creates a Student record and stores it.
     * Returns the new Student so callers can work with it further.
     */
    private Student addStudentInternal(String name, String email) {
        String id = "STU" + String.format("%03d", nextIdSuffix++);
        Student s = new Student(id, name, email);
        students.put(id, s);
        return s;
    }

    // ---------------------------------------------------------------
    // UI construction
    // ---------------------------------------------------------------

    private void initUI() {
        // Menu bar
        setJMenuBar(buildMenuBar());

        // Main panel with padding
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Header
        JLabel header = new JLabel("Student Management System", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(new Color(30, 80, 160));
        header.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        main.add(header, BorderLayout.NORTH);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Student Management", buildStudentManagementTab());
        tabbedPane.addTab("Course Enrollment", buildEnrollmentTab());
        tabbedPane.addTab("Grade Management", buildGradeTab());
        main.add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JLabel status = new JLabel(" Ready");
        status.setBorder(BorderFactory.createEtchedBorder());
        main.add(status, BorderLayout.SOUTH);

        add(main);
    }

    // -------  Menu bar  -------

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu studentMenu = new JMenu("Students");
        JMenuItem addItem = new JMenuItem("Add Student");
        JMenuItem updateItem = new JMenuItem("Update Student");
        JMenuItem viewItem = new JMenuItem("View All Students");
        addItem.addActionListener(e -> showAddStudentDialog());
        updateItem.addActionListener(e -> showUpdateStudentDialog());
        viewItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        studentMenu.add(addItem);
        studentMenu.add(updateItem);
        studentMenu.add(viewItem);

        JMenu enrollMenu = new JMenu("Enrollment");
        JMenuItem enrollItem = new JMenuItem("Enroll Student");
        enrollItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        enrollMenu.add(enrollItem);

        JMenu gradeMenu = new JMenu("Grades");
        JMenuItem gradeItem = new JMenuItem("Assign Grades");
        gradeItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        gradeMenu.add(gradeItem);

        bar.add(fileMenu);
        bar.add(studentMenu);
        bar.add(enrollMenu);
        bar.add(gradeMenu);
        return bar;
    }

    // -------  Tab 1: Student Management  -------

    private JPanel buildStudentManagementTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton addBtn = new JButton("Add Student");
        JButton updateBtn = new JButton("Update Student");
        JButton deleteBtn = new JButton("Delete Student");
        styleButton(addBtn, new Color(46, 125, 50));
        styleButton(updateBtn, new Color(21, 101, 192));
        styleButton(deleteBtn, new Color(183, 28, 28));
        addBtn.addActionListener(e -> showAddStudentDialog());
        updateBtn.addActionListener(e -> showUpdateStudentDialog());
        deleteBtn.addActionListener(e -> deleteSelectedStudent());
        toolbar.add(addBtn);
        toolbar.add(updateBtn);
        toolbar.add(deleteBtn);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Student ID", "Name", "Email", "Enrolled Courses"};
        studentTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setRowHeight(24);
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        return panel;
    }

    // -------  Tab 2: Course Enrollment  -------

    private JPanel buildEnrollmentTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Course selector
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        form.add(new JLabel("Select Course:"), gc);
        enrollCourseCombo = new JComboBox<>(COURSES);
        gc.gridx = 1; gc.weightx = 1;
        form.add(enrollCourseCombo, gc);

        // Student selector
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        form.add(new JLabel("Select Student:"), gc);
        enrollStudentCombo = new JComboBox<>();
        gc.gridx = 1; gc.weightx = 1;
        form.add(enrollStudentCombo, gc);

        // Enroll button
        JButton enrollBtn = new JButton("Enroll Student");
        styleButton(enrollBtn, new Color(21, 101, 192));
        enrollBtn.addActionListener(e -> enrollSelectedStudent());
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 0;
        form.add(enrollBtn, gc);

        // Status label
        enrollStatusLabel = new JLabel(" ");
        enrollStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
        form.add(enrollStatusLabel, gc);

        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    // -------  Tab 3: Grade Management  -------

    private JPanel buildGradeTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Student selector
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        form.add(new JLabel("Select Student:"), gc);
        gradeStudentCombo = new JComboBox<>();
        gradeStudentCombo.addActionListener(e -> refreshGradeCourseCombo());
        gc.gridx = 1; gc.weightx = 1;
        form.add(gradeStudentCombo, gc);

        // Course selector (filtered by enrollment)
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        form.add(new JLabel("Select Course:"), gc);
        gradeCoursCombo = new JComboBox<>();
        gc.gridx = 1; gc.weightx = 1;
        form.add(gradeCoursCombo, gc);

        // Grade selector
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        form.add(new JLabel("Assign Grade:"), gc);
        gradeValueCombo = new JComboBox<>(GRADES);
        gc.gridx = 1; gc.weightx = 1;
        form.add(gradeValueCombo, gc);

        // Assign button
        JButton assignBtn = new JButton("Assign Grade");
        styleButton(assignBtn, new Color(46, 125, 50));
        assignBtn.addActionListener(e -> assignGrade());
        gc.gridx = 1; gc.gridy = 3; gc.weightx = 0;
        form.add(assignBtn, gc);

        panel.add(form, BorderLayout.NORTH);

        // Grade table (shows grades for the selected student)
        String[] cols = {"Course", "Grade"};
        gradeTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable gradeTable = new JTable(gradeTableModel);
        gradeTable.setRowHeight(24);
        gradeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gradeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(new JScrollPane(gradeTable), BorderLayout.CENTER);

        return panel;
    }

    // ---------------------------------------------------------------
    // Dialog helpers
    // ---------------------------------------------------------------

    /** Displays a form dialog for adding a new student. */
    private void showAddStudentDialog() {
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        JPanel form = buildFormPanel(
            new String[]{"Full Name:", "Email Address:"},
            new JTextField[]{nameField, emailField}
        );

        int result = JOptionPane.showConfirmDialog(
            this, form, "Add New Student",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            // Validation
            if (name.isEmpty() || email.isEmpty()) {
                showError("Both name and email are required.");
                return;
            }
            if (!email.contains("@")) {
                showError("Please enter a valid email address.");
                return;
            }

            addStudentInternal(name, email);
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Student added successfully.", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Displays a form dialog for updating an existing student record. */
    private void showUpdateStudentDialog() {
        if (students.isEmpty()) {
            showError("No students available to update.");
            return;
        }

        // Let the admin pick which student to update
        String[] ids = students.keySet().toArray(new String[0]);
        String[] labels = students.values().stream()
            .map(s -> s.id + " - " + s.name)
            .toArray(String[]::new);

        String chosen = (String) JOptionPane.showInputDialog(
            this, "Select a student to update:", "Update Student",
            JOptionPane.PLAIN_MESSAGE, null, labels, labels[0]
        );
        if (chosen == null) return;

        // Find matching student
        String chosenId = ids[Arrays.asList(labels).indexOf(chosen)];
        Student student = students.get(chosenId);

        JTextField nameField = new JTextField(student.name, 20);
        JTextField emailField = new JTextField(student.email, 20);

        JPanel form = buildFormPanel(
            new String[]{"Full Name:", "Email Address:"},
            new JTextField[]{nameField, emailField}
        );

        int result = JOptionPane.showConfirmDialog(
            this, form, "Update Student: " + student.id,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                showError("Both name and email are required.");
                return;
            }
            if (!email.contains("@")) {
                showError("Please enter a valid email address.");
                return;
            }

            student.name = name;
            student.email = email;
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Student updated successfully.", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Deletes the student currently selected in the student table. */
    private void deleteSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a student to delete.");
            return;
        }
        String id = (String) studentTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this, "Delete student " + id + "? This action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            students.remove(id);
            refreshAllViews();
        }
    }

    // ---------------------------------------------------------------
    // Enrollment logic
    // ---------------------------------------------------------------

    /** Enrolls the selected student in the selected course. */
    private void enrollSelectedStudent() {
        String studentLabel = (String) enrollStudentCombo.getSelectedItem();
        String course = (String) enrollCourseCombo.getSelectedItem();

        if (studentLabel == null || course == null) {
            enrollStatusLabel.setForeground(Color.RED);
            enrollStatusLabel.setText("Please select both a course and a student.");
            return;
        }

        // Extract the student ID prefix from label "STU001 - Name"
        String studentId = studentLabel.split(" - ")[0].trim();
        Student student = students.get(studentId);

        if (student == null) {
            showError("Student not found. Please refresh the view.");
            return;
        }
        if (student.enrollment.containsKey(course)) {
            enrollStatusLabel.setForeground(new Color(183, 28, 28));
            enrollStatusLabel.setText(student.name + " is already enrolled in this course.");
            return;
        }

        student.enrollment.put(course, null);
        enrollStatusLabel.setForeground(new Color(46, 125, 50));
        enrollStatusLabel.setText("Success: " + student.name + " enrolled in " + course);
        refreshAllViews();
    }

    // ---------------------------------------------------------------
    // Grade logic
    // ---------------------------------------------------------------

    /** Refreshes the course combo box based on the selected student's enrollment. */
    private void refreshGradeCourseCombo() {
        gradeCoursCombo.removeAllItems();
        gradeTableModel.setRowCount(0);

        String studentLabel = (String) gradeStudentCombo.getSelectedItem();
        if (studentLabel == null) return;

        String studentId = studentLabel.split(" - ")[0].trim();
        Student student = students.get(studentId);
        if (student == null) return;

        for (String course : student.enrollment.keySet()) {
            gradeCoursCombo.addItem(course);
        }

        // Populate the grade review table
        for (Map.Entry<String, String> entry : student.enrollment.entrySet()) {
            gradeTableModel.addRow(new Object[]{
                entry.getKey(),
                entry.getValue() != null ? entry.getValue() : "Not graded"
            });
        }
    }

    /** Assigns the chosen grade to the selected student and course. */
    private void assignGrade() {
        String studentLabel = (String) gradeStudentCombo.getSelectedItem();
        String course = (String) gradeCoursCombo.getSelectedItem();
        String grade = (String) gradeValueCombo.getSelectedItem();

        if (studentLabel == null || course == null || grade == null) {
            showError("Please select a student, a course, and a grade.");
            return;
        }

        String studentId = studentLabel.split(" - ")[0].trim();
        Student student = students.get(studentId);
        if (student == null || !student.enrollment.containsKey(course)) {
            showError("The selected student is not enrolled in this course.");
            return;
        }

        student.enrollment.put(course, grade);
        refreshAllViews();
        JOptionPane.showMessageDialog(this, "Grade " + grade + " assigned to "
            + student.name + " for " + course, "Grade Assigned",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------------------------------------------------------
    // Refresh / sync helpers
    // ---------------------------------------------------------------

    /**
     * Refreshes every view (table and combo boxes) to reflect the
     * current state of the in-memory student map. Called after any
     * create, update, delete, enroll, or grade operation.
     */
    private void refreshAllViews() {
        refreshStudentTable();
        refreshEnrollCombos();
        refreshGradeCombos();
    }

    private void refreshStudentTable() {
        studentTableModel.setRowCount(0);
        for (Student s : students.values()) {
            int count = s.enrollment.size();
            studentTableModel.addRow(new Object[]{
                s.id, s.name, s.email,
                count + (count == 1 ? " course" : " courses")
            });
        }
    }

    private void refreshEnrollCombos() {
        enrollStudentCombo.removeAllItems();
        for (Student s : students.values()) {
            enrollStudentCombo.addItem(s.id + " - " + s.name);
        }
    }

    private void refreshGradeCombos() {
        String prev = (String) gradeStudentCombo.getSelectedItem();
        gradeStudentCombo.removeAllItems();
        for (Student s : students.values()) {
            gradeStudentCombo.addItem(s.id + " - " + s.name);
        }
        if (prev != null) gradeStudentCombo.setSelectedItem(prev);
        refreshGradeCourseCombo();
    }

    // ---------------------------------------------------------------
    // Utility helpers
    // ---------------------------------------------------------------

    /** Applies a consistent style to action buttons. */
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }

    /** Builds a two-column label/field grid panel for dialog forms. */
    private JPanel buildFormPanel(String[] labels, JTextField[] fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel(labels[i]), gc);
            gc.gridx = 1; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;
            panel.add(fields[i], gc);
            gc.weightx = 0;
        }
        return panel;
    }

    /** Displays a standard error dialog. */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ---------------------------------------------------------------
    // Entry point
    // ---------------------------------------------------------------

    public static void main(String[] args) {
        // Use the system look and feel for a native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to the default Swing look and feel
        }

        SwingUtilities.invokeLater(() -> new StudentManagementSystem().setVisible(true));
    }
}

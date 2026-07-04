# Student Management System

A Swing-based GUI application designed for managing student records, course enrollment, and grade assignments. This application was developed by Austine as a Unit 7 Assignment for the course CS 1102 - Programming 1.

The entire desktop application interface and core administrative logic are encapsulated within the `StudentManagementSystem.java` file.

## 🚀 Key Features

### Student Directory Management
- Add new student profiles with designated names and email addresses
- Update existing student records
- Permanently delete selected students from the database

### Course Enrollment Portal
- Link registered students to active courses offered within the system
- Manage student course enrollments efficiently

### Grade Center
- Assign standard academic letter grades to students for their specific enrolled courses
- View interactive performance summaries immediately

### Data Seeding
- Automatically pre-populates sample student records on startup
- Test the interface immediately without manual data entry

## 🛠️ Architecture & Tech Stack

- **Language:** Java
- **GUI Framework:** Java Swing and AWT components
  - Multi-tabbed layout (JTabbedPane)
  - Window frames (JFrame)
  - Structural grids (GridBagLayout)
- **Data Storage:** In-memory collection models
  - LinkedHashMap for student directories
  - Individual course enrollment mapping with predictable iteration order

## 📚 Available Academic Courses

The system contains a pre-defined catalog of courses available for student registration:

| Course Code | Course Name |
|-------------|-------------|
| CS1101 | Intro to Computer Science |
| CS1102 | Programming 1 |
| MATH1201 | College Algebra |
| MATH1280 | Intro to Statistics |
| ENGL1102 | English Composition |

## 📊 Supported Grading Scale

The grading center supports the assignment of the following standard evaluation marks:

- A
- A-
- B+
- B
- B-
- C+
- C
- C-
- D
- F

## 🔧 How to Run

### Prerequisites
Ensure you have the Java Development Kit (JDK 8 or higher) installed on your system.

### Compilation and Execution

1. **Compile the source file:**
   ```bash
   javac StudentManagementSystem.java

2. **Execute the Application**
   ```bash
   java StudentManagementSystem

The graphical window will launch, and you can begin managing student records immediately.

## Project Structure
StudentManagementSystem/
└── StudentManagementSystem.java    # Contains entire application logic and GUI

## Usage Notes

The application starts with pre-loaded sample student data

All data is stored in-memory and will be lost when the application closes

Each tab provides specific functionality:

Student Directory: Manage student profiles

Course Enrollment: Register students for courses

Grade Center: Assign and view grades

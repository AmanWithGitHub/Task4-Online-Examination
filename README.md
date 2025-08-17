📝 Online Examination System

The Online Examination System is a simple yet effective application developed using Java Swing for the graphical user interface and MySQL for the backend database.
It provides a complete solution for conducting online exams with secure login, user management, timed exams, randomized questions, and automatic scoring.

🚀 Features
🔑 User Authentication

Secure login with username and password.

Credentials validated against the MySQL database.

👤 User Profile Management

View and update personal details (name).

Change password (saved directly to the database).

📝 Online Examination

Start a timed multiple-choice exam.

Randomized questions are fetched from the database for a unique exam each time.

Automatic scoring – answers are validated against the database, and results are saved.

⏳ Timer

Built-in exam timer to enforce a strict time limit.

🗄️ Technology Stack

Language: Java

User Interface: Java Swing

Database: MySQL

Connectivity: JDBC (Java Database Connectivity) Driver

Design: Object-Oriented (separate classes for each component)

📂 Project Structure

User.java – Represents user details and actions.

Question.java – Handles exam questions.

LoginFrame.java – Manages user login.

ProfileFrame.java – Manages profile updates.

ExamFrame.java – Conducts the exam.

DatabaseManager.java – Handles database operations.

🎯 Key Highlights

Randomized questions for fairness.

Automatic result calculation and storage.

Clean object-oriented structure.

Secure user authentication.

🏆 Conclusion

The Online Examination System provides a complete solution for digital exams, combining a robust MySQL database, Java Swing interface, and JDBC connectivity to create a reliable and user-friendly platform.

Online Examination System
This is a simple Online Examination System developed using Java Swing for the graphical user interface and MySQL for the backend database.

Features
User Authentication: The system provides a secure login for users with a username and password. It validates credentials against a MySQL database.

User Profile Management: After logging in, users can view and update their name. They can also change their password. The changes are saved to the users table in the database.

Online Examination: Users can start a timed exam with multiple-choice questions.

Randomized Questions: Questions are loaded randomly from the database to ensure a different exam experience each time.

Automatic Scoring: The system calculates the user's score automatically by comparing their answers to the correct answers stored in the database. The final score is then saved to the results table.

Timer: An exam timer provides a time limit for the user.

Technology Stack
Language: Java.

User Interface: Java Swing.

Backend: MySQL Database.

Connectivity: JDBC (Java Database Connectivity) Driver.

Object-Oriented Design: The project is structured using multiple classes (User, Question, LoginFrame, ProfileFrame, ExamFrame, DatabaseManager) to manage different aspects of the application.

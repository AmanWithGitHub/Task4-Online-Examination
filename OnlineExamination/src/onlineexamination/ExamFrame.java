import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExamFrame extends JFrame {

    private User currentUser;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String[] userAnswers;
    
    // GUI Components
    private JLabel timerLabel;
    private JTextArea questionArea;
    private ButtonGroup optionsGroup;
    private JRadioButton optionA, optionB, optionC, optionD;
    private JButton nextButton;
    private Timer timer;
    private int secondsRemaining = 60; // 60 seconds for the exam

    public ExamFrame(User user) {
        this.currentUser = user;
        setTitle("Online Exam - User: " + currentUser.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Load questions from the database first
        questionList = loadQuestions();
        if (questionList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions found in the database. Exiting.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        userAnswers = new String[questionList.size()];
        
        // GUI Setup
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Timer Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timerLabel = new JLabel("Time Remaining: 00:60");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(timerLabel);
        
        // Question Panel
        JPanel questionPanel = new JPanel(new BorderLayout(5, 5));
        questionArea = new JTextArea();
        questionArea.setFont(new Font("Arial", Font.PLAIN, 16));
        questionArea.setEditable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionPanel.add(new JScrollPane(questionArea), BorderLayout.CENTER);
        
        // Options Panel
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        optionsGroup = new ButtonGroup();
        optionA = new JRadioButton("A");
        optionB = new JRadioButton("B");
        optionC = new JRadioButton("C");
        optionD = new JRadioButton("D");
        optionsGroup.add(optionA);
        optionsGroup.add(optionB);
        optionsGroup.add(optionC);
        optionsGroup.add(optionD);
        optionsPanel.add(optionA);
        optionsPanel.add(optionB);
        optionsPanel.add(optionC);
        optionsPanel.add(optionD);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nextButton = new JButton("Next");
        buttonPanel.add(nextButton);
        
        // Add panels to the main frame
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(questionPanel, BorderLayout.CENTER);
        mainPanel.add(optionsPanel, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        
        // Start the timer
        startTimer();
        
        // Display the first question
        displayQuestion();
        
        // Action listener for Next/Submit button
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAnswer();
                currentQuestionIndex++;
                if (currentQuestionIndex < questionList.size()) {
                    displayQuestion();
                } else {
                    submitExam();
                }
            }
        });
    }

    private List<Question> loadQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY RAND()"; // Get random questions
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                questions.add(new Question(
                    rs.getInt("question_id"),
                    rs.getString("question_text"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct_option")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading questions from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return questions;
    }
    
    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(currentQuestionIndex);
            questionArea.setText((currentQuestionIndex + 1) + ". " + currentQuestion.getQuestionText());
            optionA.setText("A) " + currentQuestion.getOptionA());
            optionB.setText("B) " + currentQuestion.getOptionB());
            optionC.setText("C) " + currentQuestion.getOptionC());
            optionD.setText("D) " + currentQuestion.getOptionD());
            optionsGroup.clearSelection(); // Clear previous selection
            
            if (currentQuestionIndex == questionList.size() - 1) {
                nextButton.setText("Submit");
            }
        }
    }
    
    private void saveAnswer() {
        String selectedAnswer = null;
        if (optionA.isSelected()) selectedAnswer = "A";
        else if (optionB.isSelected()) selectedAnswer = "B";
        else if (optionC.isSelected()) selectedAnswer = "C";
        else if (optionD.isSelected()) selectedAnswer = "D";
        
        userAnswers[currentQuestionIndex] = selectedAnswer;
    }

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsRemaining--;
                timerLabel.setText(String.format("Time Remaining: 00:%02d", secondsRemaining));
                if (secondsRemaining <= 0) {
                    timer.stop();
                    JOptionPane.showMessageDialog(ExamFrame.this, "Time's up! Your exam will be submitted automatically.", "Time's Up", JOptionPane.INFORMATION_MESSAGE);
                    submitExam();
                }
            }
        });
        timer.start();
    }
    
    private void submitExam() {
        timer.stop(); // Stop the timer in case of early submit
        
        // Calculate the score
        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
            if (userAnswers[i] != null && userAnswers[i].equals(question.getCorrectOption())) {
                score++;
            }
        }
        
        // Save the result to the database
        String sql = "INSERT INTO results (user_id, score) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, currentUser.getUserId());
            pstmt.setDouble(2, score);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Exam submitted successfully! Your score is: " + score + " out of " + questionList.size(), "Exam Complete", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving exam results.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // Redirect to profile or login screen
        new LoginFrame().setVisible(true);
        dispose();
    }
}
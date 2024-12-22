package Guesserprogram;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class Guesser {
    private static List<String> wordList = new ArrayList<>();
    private static List<String> remainingWords = new ArrayList<>();
    private static JTextArea outputArea;
    private static JTextField feedbackField;
    private static boolean isFirstGuess = true;
    private static String currentGuess = "";
    private static String previousGuess = "";  // Added to track previous guess

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader("Guesserprogram/words.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (word.length() == 5) wordList.add(word);
            }
            remainingWords.addAll(wordList);
            SwingUtilities.invokeLater(() -> createAndShowGUI());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading words file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void askToRestart() {
        outputArea.append("Would you like to restart the game? (Type YES to restart, NO to quit)\n");

        // Remove existing action listeners
        for (ActionListener al : feedbackField.getActionListeners()) {
            feedbackField.removeActionListener(al);
        }

        // Add new restart-specific action listener
        feedbackField.addActionListener(e -> {
            String response = feedbackField.getText().trim().toUpperCase();
            feedbackField.setText("");

            if (response.equals("YES")) {
                resetGame();
                // Restore the original guess processing listener
                for (ActionListener al : feedbackField.getActionListeners()) {
                    feedbackField.removeActionListener(al);
                }
                feedbackField.addActionListener(e2 -> processGuess());
            } else if (response.equals("NO")) {
                JOptionPane.showMessageDialog(null, "Thanks for coming! See you next time!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            } else {
                outputArea.append("Invalid response. Please type YES or NO.\n");
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Wordle Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        feedbackField = new JTextField(10);
        JButton guessButton = new JButton("Submit Feedback");
        guessButton.addActionListener(e -> processGuess());
        feedbackField.addActionListener(e -> processGuess());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        inputPanel.add(new JLabel("Enter Feedback (G=Green, Y=Yellow, B=Black): "));
        inputPanel.add(feedbackField);
        inputPanel.add(guessButton);

        mainPanel.add(new JLabel("G = Correct letter, correct position | Y = Correct letter, wrong position | B = Letter not in word"),
                BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
        showNextGuess();
    }

    private static void processGuess() {
        String feedback = feedbackField.getText().trim().toUpperCase();
        feedbackField.setText("");

        if (!feedback.matches("[GYB]{5}")) {
            JOptionPane.showMessageDialog(null, "Please enter exactly 5 characters using only G, Y, and B");
            return;
        }

        outputArea.append("Your answer for the guess was: (" + feedback + ")\n");

        if (feedback.equals("GGGGG")) {
            outputArea.append("Congrats u have found the word!\n");
            askToRestart();
            return;
        }

        // Remove the current guess from remaining words before filtering
        remainingWords.remove(currentGuess.toLowerCase());

        // Filter the remaining words
        List<String> filteredWords = filterWords(remainingWords, currentGuess.toUpperCase(), feedback);
        remainingWords = filteredWords;

        if (remainingWords.isEmpty()) {
            outputArea.append("No possible words left. Please check your feedback.\n");
            askToRestart();
        } else {
            previousGuess = currentGuess;  // Store the current guess before showing next
            showNextGuess();
        }
    }

    private static void resetGame() {
        remainingWords.clear();
        remainingWords.addAll(wordList);
        isFirstGuess = true;
        previousGuess = "";
        showNextGuess();
    }

    private static void showNextGuess() {
        if (remainingWords.isEmpty()) {
            outputArea.append("No valid words remaining.\n");
            askToRestart();
            return;
        }

        currentGuess = isFirstGuess ? "ARISE" : remainingWords.get(0).toUpperCase();

        // Check if we're about to suggest the same word again
        if (currentGuess.equals(previousGuess) && remainingWords.size() > 1) {
            remainingWords.remove(0);  // Remove the repeated word
            currentGuess = remainingWords.get(0).toUpperCase();  // Get the next word
        }

        isFirstGuess = false;
        outputArea.append("Guess: " + currentGuess + "\n");
    }

    private static List<String> filterWords(List<String> words, String guess, String feedback) {
        List<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            if (isWordValid(word.toUpperCase(), guess, feedback)) filteredWords.add(word);
        }
        return filteredWords;
    }

    private static boolean isWordValid(String word, String guess, String feedback) {
        Map<Character, Integer> letterCounts = new HashMap<>();
        Map<Character, Integer> minRequired = new HashMap<>();
        Set<Character> exactPositions = new HashSet<>();
        Set<Character> wrongPositions = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            char guessChar = guess.charAt(i);
            char feedbackChar = feedback.charAt(i);

            if (feedbackChar == 'G') {
                if (word.charAt(i) != guessChar) return false;
                exactPositions.add(guessChar);
                minRequired.merge(guessChar, 1, Integer::sum);
            } else if (feedbackChar == 'Y') {
                if (word.charAt(i) == guessChar) return false;
                wrongPositions.add(guessChar);
                minRequired.merge(guessChar, 1, Integer::sum);
            }
            letterCounts.merge(word.charAt(i), 1, Integer::sum);
        }

        for (int i = 0; i < 5; i++) {
            char guessChar = guess.charAt(i);
            if (feedback.charAt(i) == 'B' && !exactPositions.contains(guessChar) &&
                    !wrongPositions.contains(guessChar) && letterCounts.getOrDefault(guessChar, 0) > 0) {
                return false;
            }
        }

        for (Map.Entry<Character, Integer> entry : minRequired.entrySet()) {
            if (letterCounts.getOrDefault(entry.getKey(), 0) < entry.getValue()) return false;
        }
        return true;
    }
}
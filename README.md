# Wordle-Word-Guesser

This code extends the functionality of the Wordle solver program with additional enhancements:

---

### **Overview of Changes**
1. **Restart Option**:  
   - After the correct word is guessed (`GGGGG`) or no valid guesses remain, users can restart the game or exit.
   - A method `askToRestart` is introduced to handle this flow.

2. **Avoiding Repeated Suggestions**:  
   - Tracks the `previousGuess` to prevent suggesting the same word consecutively.
   - If the next suggestion equals the `previousGuess`, it skips to the next possible word.

3. **Feedback for Invalid Scenarios**:  
   - Provides clarity when no words are left due to invalid feedback or filtering.
   - Suggests restarting in such cases.

---

### **Key Enhancements**

#### 1. **Restart Mechanism**
The game now prompts users to restart or quit:
- **Method: `askToRestart`**
  - Temporarily removes regular feedback handling (`processGuess`) and waits for a YES/NO response.
  - Resets the game with `resetGame()` on "YES".
  - Exits the program on "NO".
  - Invalid responses prompt clarification.

#### 2. **Avoiding Repeated Words**
- Maintains a `previousGuess` variable to store the last suggested guess.
- Before suggesting a new guess:
  - If `currentGuess` equals `previousGuess` and more words remain:
    - Skips the current word and selects the next.
- This ensures unique and meaningful suggestions.

#### 3. **Filtering Logic Refinements**
- Updates the `remainingWords` list more rigorously:
  - Ensures the current guess is removed before filtering.
  - Prevents reprocessing redundant words.

---

### **Code Structure and New Functionality**

#### **New/Updated Methods**
| **Method**         | **Purpose**                                                                                          |
|---------------------|-----------------------------------------------------------------------------------------------------|
| `askToRestart`      | Handles the restart or quit decision. Overrides regular feedback handling temporarily.               |
| `resetGame`         | Restores the initial game state. Clears and reloads `remainingWords` and resets tracking variables.  |
| `showNextGuess`     | Ensures `previousGuess` and `currentGuess` differ, avoids redundancy when suggesting words.          |

#### **Existing Methods with Changes**
| **Method**         | **Change**                                                                                          |
|---------------------|-----------------------------------------------------------------------------------------------------|
| `processGuess`      | Updates to handle restarting or avoiding repeated guesses.                                           |
| `showNextGuess`     | Compares `currentGuess` with `previousGuess` and adjusts suggestions.                               |

---

### **Sample Workflow with Updates**

#### 1. First Guess
- Outputs the first word, e.g., "ARISE."
- Tracks `previousGuess = ""`.

#### 2. Feedback Handling
- If feedback is valid (`[GYB]{5}`), the guess is processed:
  - Filters possible words based on feedback.
  - Updates the word list by removing `currentGuess`.

#### 3. Avoiding Repeats
- If the next guess matches the `previousGuess`:
  - Removes the redundant word.
  - Selects the next valid word.

#### 4. Winning or Exhausting Options
- **Win** (`GGGGG`): Congratulates the user and prompts to restart or exit.
- **No Words Left**: Suggests restarting or warns of invalid feedback.

---

### **Code Walkthrough**

1. **Initialization and GUI Setup**:
   - Loads words and initializes variables and GUI components.

2. **First Guess (`showNextGuess`)**:
   - Starts with the word "ARISE" as the default.

3. **Feedback Processing (`processGuess`)**:
   - Removes invalid or already-used words.
   - Applies filtering logic with `filterWords` and `isWordValid`.

4. **Restart Prompt (`askToRestart`)**:
   - Captures YES/NO input for restart.
   - Quits or resets based on the user’s choice.

---

### **Example Interaction**

#### Input Flow
1. **Program's Guess**: ARISE  
2. **User Feedback**: GYYBB  
3. **Filtered Words**: Updated suggestions.

#### End of Game
- **Win Feedback**: GGGGG  
  - **Output**: "Congrats! Restart game? YES or NO."

---

This enhanced implementation ensures better interactivity and robustness in the Wordle-solving process. It prevents repetitive suggestions and offers a structured restart mechanism, creating a smoother user experience.

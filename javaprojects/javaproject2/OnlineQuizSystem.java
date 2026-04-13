import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OnlineQuizSystem {
    private static final Path DATA_FILE = Paths.get("quiz_questions.db");
    private static final int DEFAULT_TIME_LIMIT_SECONDS = 20;

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<Question> questions = loadQuestions();

        System.out.println("=====================================");
        System.out.println("      Online Quiz & Assessment       ");
        System.out.println("=====================================");

        while (true) {
            System.out.println();
            System.out.println("1. Admin panel");
            System.out.println("2. Take quiz");
            System.out.println("3. Exit");
            String choice = readLine(reader, "Choose an option: ");

            if ("1".equals(choice)) {
                questions = adminPanel(reader, questions);
            } else if ("2".equals(choice)) {
                if (questions.isEmpty()) {
                    System.out.println("No questions available yet. Ask the admin to add some first.");
                } else {
                    takeQuiz(reader, questions);
                }
            } else if ("3".equals(choice)) {
                System.out.println("Goodbye.");
                break;
            } else {
                System.out.println("Please choose 1, 2, or 3.");
            }
        }
    }

    private static List<Question> adminPanel(BufferedReader reader, List<Question> questions) {
        while (true) {
            System.out.println();
            System.out.println("--- Admin Panel ---");
            System.out.println("1. Add a question");
            System.out.println("2. View saved questions");
            System.out.println("3. Back");
            String choice = readLine(reader, "Choose an option: ");

            if ("1".equals(choice)) {
                Question question = createQuestion(reader);
                questions.add(question);
                saveQuestions(questions);
                System.out.println("Question saved successfully.");
            } else if ("2".equals(choice)) {
                if (questions.isEmpty()) {
                    System.out.println("No questions saved yet.");
                } else {
                    for (int i = 0; i < questions.size(); i++) {
                        Question q = questions.get(i);
                        System.out.println((i + 1) + ". " + q.questionText);
                    }
                }
            } else if ("3".equals(choice)) {
                return questions;
            } else {
                System.out.println("Please choose 1, 2, or 3.");
            }
        }
    }

    private static Question createQuestion(BufferedReader reader) {
        System.out.println();
        System.out.println("Enter question details.");
        String text = readNonEmptyLine(reader, "Question: ");
        List<String> options = new ArrayList<>();
        options.add(readNonEmptyLine(reader, "Option 1: "));
        options.add(readNonEmptyLine(reader, "Option 2: "));
        options.add(readNonEmptyLine(reader, "Option 3: "));
        options.add(readNonEmptyLine(reader, "Option 4: "));

        int correctIndex = readIntInRange(reader, "Correct option number (1-4): ", 1, 4) - 1;
        int timeLimit = readOptionalInt(reader, "Time limit in seconds (press Enter for default 20): ", DEFAULT_TIME_LIMIT_SECONDS);

        return new Question(text, options, correctIndex, timeLimit);
    }

    private static void takeQuiz(BufferedReader reader, List<Question> sourceQuestions) {
        List<Question> questions = new ArrayList<>(sourceQuestions);
        Collections.shuffle(questions);

        System.out.println();
        System.out.println("Quiz started. Answer each question before the timer runs out.");
        System.out.println("Type the option number and press Enter.");

        int score = 0;
        int correct = 0;
        int wrong = 0;
        int timedOut = 0;
        List<String> missedTopics = new ArrayList<>();
        List<String> reviewLog = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.println();
            System.out.println("Question " + (i + 1) + " of " + questions.size());
            System.out.println(q.questionText);
            for (int j = 0; j < q.options.size(); j++) {
                System.out.println((j + 1) + ". " + q.options.get(j));
            }
            System.out.println("Time limit: " + q.timeLimitSeconds + " seconds");

            String answer = readTimedLine(reader, q.timeLimitSeconds);
            if (answer == null) {
                timedOut++;
                reviewLog.add("Timed out: " + q.questionText + " | Correct answer: " + q.options.get(q.correctIndex));
                System.out.println("Time is up. Correct answer: " + q.options.get(q.correctIndex));
                continue;
            }

            int chosenIndex = parseChoice(answer);
            if (chosenIndex == -1) {
                wrong++;
                missedTopics.add(q.questionText);
                reviewLog.add("Invalid answer for: " + q.questionText + " | Correct answer: " + q.options.get(q.correctIndex));
                System.out.println("Invalid input. Correct answer: " + q.options.get(q.correctIndex));
                continue;
            }

            if (chosenIndex == q.correctIndex) {
                score++;
                correct++;
                reviewLog.add("Correct: " + q.questionText);
                System.out.println("Correct.");
            } else {
                wrong++;
                missedTopics.add(q.questionText);
                reviewLog.add("Wrong: " + q.questionText + " | Correct answer: " + q.options.get(q.correctIndex));
                System.out.println("Wrong. Correct answer: " + q.options.get(q.correctIndex));
            }
        }

        printSummary(questions.size(), score, correct, wrong, timedOut, missedTopics, reviewLog);
    }

    private static void printSummary(int total, int score, int correct, int wrong, int timedOut,
                                     List<String> missedTopics, List<String> reviewLog) {
        int percentage = total == 0 ? 0 : (int) Math.round((score * 100.0) / total);
        String grade;
        String analysis;

        if (percentage >= 85) {
            grade = "Excellent";
            analysis = "Strong performance. You have a good grasp of the quiz topics.";
        } else if (percentage >= 70) {
            grade = "Good";
            analysis = "Solid performance, but a little more revision can make it stronger.";
        } else if (percentage >= 50) {
            grade = "Average";
            analysis = "You know some of the material, but there is clear room for improvement.";
        } else {
            grade = "Needs Improvement";
            analysis = "Focus on the basics and practice more sample questions.";
        }

        System.out.println();
        System.out.println("=====================================");
        System.out.println("             Result Summary          ");
        System.out.println("=====================================");
        System.out.println("Total Questions : " + total);
        System.out.println("Correct Answers : " + correct);
        System.out.println("Wrong Answers   : " + wrong);
        System.out.println("Timed Out       : " + timedOut);
        System.out.println("Score           : " + score + "/" + total);
        System.out.println("Percentage      : " + percentage + "%");
        System.out.println("Performance     : " + grade);
        System.out.println("Analysis        : " + analysis);

        if (!missedTopics.isEmpty()) {
            System.out.println();
            System.out.println("Questions to review:");
            for (String item : missedTopics) {
                System.out.println("- " + item);
            }
        }

        System.out.println();
        System.out.println("Detailed attempt log:");
        for (String line : reviewLog) {
            System.out.println("- " + line);
        }
    }

    private static List<Question> loadQuestions() {
        List<Question> questions = new ArrayList<>();
        if (!Files.exists(DATA_FILE)) {
            return questions;
        }

        try {
            List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                Question question = Question.fromStorageLine(line);
                if (question != null) {
                    questions.add(question);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load saved questions: " + e.getMessage());
        }
        return questions;
    }

    private static void saveQuestions(List<Question> questions) {
        List<String> lines = new ArrayList<>();
        for (Question q : questions) {
            lines.add(q.toStorageLine());
        }

        try {
            Files.write(DATA_FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not save questions: " + e.getMessage());
        }
    }

    private static String readLine(BufferedReader reader, String prompt) {
        System.out.print(prompt);
        try {
            String value = reader.readLine();
            return value == null ? "" : value.trim();
        } catch (IOException e) {
            return "";
        }
    }

    private static String readNonEmptyLine(BufferedReader reader, String prompt) {
        while (true) {
            String value = readLine(reader, prompt);
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    private static int readIntInRange(BufferedReader reader, String prompt, int min, int max) {
        while (true) {
            String value = readLine(reader, prompt);
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Enter a number between " + min + " and " + max + ".");
        }
    }

    private static int readOptionalInt(BufferedReader reader, String prompt, int defaultValue) {
        String value = readLine(reader, prompt);
        if (value.isBlank()) {
            return defaultValue;
        }
        try {
            int number = Integer.parseInt(value);
            return number > 0 ? number : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String readTimedLine(BufferedReader reader, int timeLimitSeconds) {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(timeLimitSeconds);
        try {
            while (System.nanoTime() < deadline) {
                if (reader.ready()) {
                    String value = reader.readLine();
                    return value == null ? "" : value.trim();
                }
                Thread.sleep(150);
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    private static int parseChoice(String input) {
        try {
            int value = Integer.parseInt(input.trim());
            if (value >= 1 && value <= 4) {
                return value - 1;
            }
        } catch (NumberFormatException ignored) {
        }
        return -1;
    }

    private static String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }

    private static class Question {
        private final String questionText;
        private final List<String> options;
        private final int correctIndex;
        private final int timeLimitSeconds;

        private Question(String questionText, List<String> options, int correctIndex, int timeLimitSeconds) {
            this.questionText = questionText;
            this.options = options;
            this.correctIndex = correctIndex;
            this.timeLimitSeconds = timeLimitSeconds;
        }

        private String toStorageLine() {
            StringBuilder builder = new StringBuilder();
            builder.append(encode(questionText)).append('|');
            builder.append(encode(options.get(0))).append('|');
            builder.append(encode(options.get(1))).append('|');
            builder.append(encode(options.get(2))).append('|');
            builder.append(encode(options.get(3))).append('|');
            builder.append(correctIndex).append('|');
            builder.append(timeLimitSeconds);
            return builder.toString();
        }

        private static Question fromStorageLine(String line) {
            String[] parts = line.split("\\|", -1);
            if (parts.length != 7) {
                return null;
            }

            try {
                String text = decode(parts[0]);
                List<String> options = new ArrayList<>();
                options.add(decode(parts[1]));
                options.add(decode(parts[2]));
                options.add(decode(parts[3]));
                options.add(decode(parts[4]));
                int correctIndex = Integer.parseInt(parts[5]);
                int timeLimit = Integer.parseInt(parts[6]);
                if (correctIndex < 0 || correctIndex > 3) {
                    return null;
                }
                if (timeLimit <= 0) {
                    timeLimit = DEFAULT_TIME_LIMIT_SECONDS;
                }
                return new Question(text, options, correctIndex, timeLimit);
            } catch (Exception e) {
                return null;
            }
        }
    }
}

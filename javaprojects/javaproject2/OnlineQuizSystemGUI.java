import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

class Question {
    public String questionText;
    public List<String> options;
    public int correctOptionIndex;
    public int timeLimit;

    public Question(String questionText, List<String> options, int correctOptionIndex, int timeLimit) {
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.timeLimit = timeLimit;
    }
}

public class OnlineQuizSystemGUI extends Application {
    private static final Path DATA_FILE = Paths.get("quiz_questions.db");
    private List<Question> questions = new ArrayList<>();
    private StackPane rootPane;

    @Override
    public void start(Stage primaryStage) {
        loadQuestions();
        rootPane = new StackPane();

        Scene scene = new Scene(rootPane, 700, 600);
        primaryStage.setTitle("Online Quiz & Assessment System");
        primaryStage.setScene(scene);
        primaryStage.show();

        showMainMenu();
    }

    private void showMainMenu() {
        VBox menuBox = new VBox(20);
        menuBox.setPadding(new Insets(40));
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-font-size: 14;");

        Label titleLabel = new Label("Online Quiz & Assessment System");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Button adminBtn = new Button("Admin Panel");
        adminBtn.setPrefSize(200, 50);
        adminBtn.setStyle("-fx-font-size: 14;");
        adminBtn.setOnAction(e -> showAdminPanel());

        Button quizBtn = new Button("Take Quiz");
        quizBtn.setPrefSize(200, 50);
        quizBtn.setStyle("-fx-font-size: 14;");
        quizBtn.setOnAction(e -> {
            if (questions.isEmpty()) {
                showAlert("No Questions", "No questions available. Ask admin to add some.");
            } else {
                startQuiz();
            }
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setPrefSize(200, 50);
        exitBtn.setStyle("-fx-font-size: 14;");
        exitBtn.setOnAction(e -> System.exit(0));

        menuBox.getChildren().addAll(titleLabel, new Separator(), adminBtn, quizBtn, exitBtn);
        rootPane.getChildren().clear();
        rootPane.getChildren().add(menuBox);
    }

    private void showAdminPanel() {
        VBox adminBox = new VBox(15);
        adminBox.setPadding(new Insets(30));
        adminBox.setStyle("-fx-font-size: 12;");

        Label titleLabel = new Label("Admin Panel");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Button addQBtn = new Button("Add Question");
        addQBtn.setPrefWidth(200);
        addQBtn.setOnAction(e -> showAddQuestionDialog());

        Button viewQBtn = new Button("View Questions");
        viewQBtn.setPrefWidth(200);
        viewQBtn.setOnAction(e -> showViewQuestions());

        Button backBtn = new Button("Back to Menu");
        backBtn.setPrefWidth(200);
        backBtn.setOnAction(e -> showMainMenu());

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.getChildren().addAll(addQBtn, viewQBtn, backBtn);

        adminBox.getChildren().addAll(titleLabel, new Separator(), btnBox);
        rootPane.getChildren().clear();
        rootPane.getChildren().add(new ScrollPane(adminBox));
    }

    private void showAddQuestionDialog() {
        Dialog<Question> dialog = new Dialog<>();
        dialog.setTitle("Add New Question");
        dialog.setHeaderText("Enter question details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField questionField = new TextField();
        questionField.setPromptText("Enter question text");
        questionField.setPrefWidth(300);

        TextField opt1Field = new TextField();
        opt1Field.setPromptText("Option 1");
        TextField opt2Field = new TextField();
        opt2Field.setPromptText("Option 2");
        TextField opt3Field = new TextField();
        opt3Field.setPromptText("Option 3");
        TextField opt4Field = new TextField();
        opt4Field.setPromptText("Option 4");

        ComboBox<Integer> correctCombo = new ComboBox<>();
        correctCombo.getItems().addAll(1, 2, 3, 4);
        correctCombo.setPromptText("Select correct option");

        TextField timeLimitField = new TextField("20");
        timeLimitField.setPrefWidth(100);

        grid.add(new Label("Question:"), 0, 0);
        grid.add(questionField, 1, 0);
        grid.add(new Label("Option 1:"), 0, 1);
        grid.add(opt1Field, 1, 1);
        grid.add(new Label("Option 2:"), 0, 2);
        grid.add(opt2Field, 1, 2);
        grid.add(new Label("Option 3:"), 0, 3);
        grid.add(opt3Field, 1, 3);
        grid.add(new Label("Option 4:"), 0, 4);
        grid.add(opt4Field, 1, 4);
        grid.add(new Label("Correct Answer:"), 0, 5);
        grid.add(correctCombo, 1, 5);
        grid.add(new Label("Time Limit (sec):"), 0, 6);
        grid.add(timeLimitField, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    String questionText = questionField.getText();
                    List<String> options = new ArrayList<>();
                    options.add(opt1Field.getText());
                    options.add(opt2Field.getText());
                    options.add(opt3Field.getText());
                    options.add(opt4Field.getText());

                    if (questionText.isEmpty() || options.stream().anyMatch(String::isEmpty) || correctCombo.getValue() == null) {
                        showAlert("Invalid Input", "Please fill all fields");
                        return null;
                    }

                    int correctIndex = correctCombo.getValue() - 1;
                    int timeLimit = Integer.parseInt(timeLimitField.getText());
                    return new Question(questionText, options, correctIndex, timeLimit);
                } catch (Exception e) {
                    showAlert("Error", "Invalid input");
                    return null;
                }
            }
            return null;
        });

        var result = dialog.showAndWait();
        if (result.isPresent()) {
            Question question = result.get();
            questions.add(question);
            saveQuestions();
            showAlert("Success", "Question added successfully!");
        }
    }

    private void showViewQuestions() {
        VBox viewBox = new VBox(15);
        viewBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Saved Questions");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        if (questions.isEmpty()) {
            viewBox.getChildren().add(new Label("No questions saved yet."));
        } else {
            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefHeight(400);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                sb.append((i + 1)).append(". ").append(q.questionText).append("\n");
                for (int j = 0; j < q.options.size(); j++) {
                    sb.append("   ").append((j + 1)).append(") ").append(q.options.get(j));
                    if (j == q.correctOptionIndex) sb.append(" [CORRECT]");
                    sb.append("\n");
                }
                sb.append("\n");
            }
            textArea.setText(sb.toString());
            viewBox.getChildren().add(textArea);
        }

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showAdminPanel());
        viewBox.getChildren().add(backBtn);

        rootPane.getChildren().clear();
        rootPane.getChildren().add(new ScrollPane(viewBox));
    }

    private void startQuiz() {
        List<Question> quizQuestions = new ArrayList<>(questions);
        Collections.shuffle(quizQuestions);

        VBox quizBox = new VBox(20);
        quizBox.setPadding(new Insets(20));
        quizBox.setStyle("-fx-font-size: 12;");

        Label titleLabel = new Label("Quiz - Question 1 of " + quizQuestions.size());
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        int[] currentIndex = {0};
        int[] score = {0};

        VBox questionContainer = new VBox(15);
        ToggleGroup toggleGroup = new ToggleGroup();
        List<RadioButton> radioButtons = new ArrayList<>();

        Consumer<Integer> showQuestion = index -> {
            if (index >= quizQuestions.size()) {
                showQuizResult(score[0], quizQuestions.size());
                return;
            }

            Question q = quizQuestions.get(index);
            titleLabel.setText("Quiz - Question " + (index + 1) + " of " + quizQuestions.size());

            questionContainer.getChildren().clear();
            toggleGroup.getToggles().clear();
            radioButtons.clear();

            Label questionLabel = new Label(q.questionText);
            questionLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
            questionLabel.setWrapText(true);
            questionContainer.getChildren().add(questionLabel);

            for (String option : q.options) {
                RadioButton rb = new RadioButton(option);
                rb.setToggleGroup(toggleGroup);
                rb.setWrapText(true);
                radioButtons.add(rb);
                questionContainer.getChildren().add(rb);
            }
        };

        showQuestion.accept(0);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button nextBtn = new Button("Next");
        nextBtn.setOnAction(e -> {
            if (toggleGroup.getSelectedToggle() == null) {
                showAlert("Warning", "Please select an answer");
                return;
            }

            int selectedIndex = radioButtons.indexOf(toggleGroup.getSelectedToggle());
            if (selectedIndex == quizQuestions.get(currentIndex[0]).correctOptionIndex) {
                score[0]++;
            }
            currentIndex[0]++;
            showQuestion.accept(currentIndex[0]);
        });

        Button quitBtn = new Button("Quit Quiz");
        quitBtn.setOnAction(e -> showMainMenu());

        buttonBox.getChildren().addAll(nextBtn, quitBtn);

        quizBox.getChildren().addAll(titleLabel, new Separator(), questionContainer, buttonBox);
        rootPane.getChildren().clear();
        rootPane.getChildren().add(new ScrollPane(quizBox));
    }

    private void showQuizResult(int score, int total) {
        VBox resultBox = new VBox(20);
        resultBox.setPadding(new Insets(40));
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setStyle("-fx-font-size: 14;");

        Label titleLabel = new Label("Quiz Completed!");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        Label scoreLabel = new Label("Your Score: " + score + " / " + total);
        scoreLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #0066cc;");

        double percentage = (score * 100.0) / total;
        Label percentageLabel = new Label(String.format("Percentage: %.1f%%", percentage));
        percentageLabel.setStyle("-fx-font-size: 16;");

        Button menuBtn = new Button("Back to Menu");
        menuBtn.setPrefSize(150, 40);
        menuBtn.setStyle("-fx-font-size: 14;");
        menuBtn.setOnAction(e -> showMainMenu());

        resultBox.getChildren().addAll(titleLabel, scoreLabel, percentageLabel, menuBtn);
        rootPane.getChildren().clear();
        rootPane.getChildren().add(resultBox);
    }

    private void saveQuestions() {
        try {
            StringBuilder sb = new StringBuilder();
            for (Question q : questions) {
                sb.append(q.questionText).append("|");
                for (int i = 0; i < q.options.size(); i++) {
                    sb.append(q.options.get(i));
                    if (i < q.options.size() - 1) sb.append(",");
                }
                sb.append("|").append(q.correctOptionIndex).append("|").append(q.timeLimit).append("\n");
            }
            Files.write(DATA_FILE, sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadQuestions() {
        try {
            if (Files.exists(DATA_FILE)) {
                List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
                for (String line : lines) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) {
                        String questionText = parts[0];
                        List<String> options = Arrays.asList(parts[1].split(","));
                        int correctIndex = Integer.parseInt(parts[2]);
                        int timeLimit = Integer.parseInt(parts[3]);
                        questions.add(new Question(questionText, options, correctIndex, timeLimit));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FunctionalInterface
    interface Consumer<T> {
        void accept(T t);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

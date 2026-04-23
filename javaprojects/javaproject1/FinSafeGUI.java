import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;

class InSufficientFundsException extends Exception {
    public InSufficientFundsException(String msg) {
        super(msg);
    }
}

class Account {
    private double balance;
    private String accountHolder;
    private ArrayList<Double> history = new ArrayList<>();

    Account(String name, double initialBal) {
        this.accountHolder = name;
        this.balance = initialBal;
    }

    public double getBalance() { return balance; }
    public String getName() { return accountHolder; }

    public void deposit(double amt) {
        if (amt <= 0) throw new IllegalArgumentException("Deposit must be positive");
        balance += amt;
        addHistory(amt);
    }

    public void processTransaction(double amount) throws InSufficientFundsException {
        if (amount < 0) throw new IllegalArgumentException("Amount can't be negative");
        if (amount > balance) throw new InSufficientFundsException("Not enough funds! Balance is " + balance + " but tried " + amount);
        balance -= amount;
        addHistory(-amount);
    }

    private void addHistory(double amt) {
        if (history.size() == 5) history.remove(0);
        history.add(amt);
    }

    public String getMiniStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Mini Statement for ").append(accountHolder).append(" ---\n");
        if (history.isEmpty()) {
            sb.append("No transactions yet\n");
        } else {
            for (int i = 0; i < history.size(); i++) {
                double t = history.get(i);
                sb.append((i + 1)).append(". ").append(t > 0 ? "Credit +" : "Debit  ").append(t).append("\n");
            }
        }
        sb.append("Current Balance: ").append(balance);
        return sb.toString();
    }
}

public class FinSafeGUI extends Application {
    private Account account;
    private Label balanceLabel;
    private TextArea historyArea;

    @Override
    public void start(Stage primaryStage) {
        // Dialog to get account holder name and initial balance
        Dialog<Pair<String, Double>> dialog = new Dialog<>();
        dialog.setTitle("FinSafe - Create Account");
        dialog.setHeaderText("Enter your account details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        TextField balanceField = new TextField();
        balanceField.setPromptText("Enter opening balance");

        grid.add(new Label("Account Holder:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Opening Balance:"), 0, 1);
        grid.add(balanceField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    String name = nameField.getText();
                    double balance = Double.parseDouble(balanceField.getText());
                    if (name.isEmpty() || balance < 0) {
                        showAlert("Invalid Input", "Please enter valid details");
                        return null;
                    }
                    return new Pair<>(name, balance);
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Balance must be a number");
                    return null;
                }
            }
            return null;
        });

        var result = dialog.showAndWait();
        if (result.isPresent()) {
            Pair<String, Double> pair = result.get();
            account = new Account(pair.getKey(), pair.getValue());
            setupMainWindow(primaryStage);
        } else {
            primaryStage.close();
        }
    }

    private void setupMainWindow(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-font-size: 12;");

        // Title
        Label titleLabel = new Label("FinSafe - Personal Finance Manager");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Account info section
        HBox infoBox = new HBox(20);
        infoBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");
        Label nameLabel = new Label("Account: " + account.getName());
        balanceLabel = new Label("Balance: ₹" + String.format("%.2f", account.getBalance()));
        balanceLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #00aa00;");
        infoBox.getChildren().addAll(nameLabel, balanceLabel);

        // Transaction section
        VBox transactionBox = new VBox(10);
        transactionBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 15;");
        transactionBox.setPrefHeight(150);
        Label transactionLabel = new Label("Quick Transaction:");
        transactionLabel.setStyle("-fx-font-weight: bold;");

        HBox depositBox = new HBox(10);
        TextField depositField = new TextField();
        depositField.setPromptText("Amount");
        depositField.setPrefWidth(150);
        Button depositBtn = new Button("Deposit");
        depositBtn.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        depositBtn.setOnAction(e -> handleDeposit(depositField));
        depositBox.getChildren().addAll(new Label("Deposit:"), depositField, depositBtn);

        HBox withdrawBox = new HBox(10);
        TextField withdrawField = new TextField();
        withdrawField.setPromptText("Amount");
        withdrawField.setPrefWidth(150);
        Button withdrawBtn = new Button("Withdraw");
        withdrawBtn.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        withdrawBtn.setOnAction(e -> handleWithdraw(withdrawField));
        withdrawBox.getChildren().addAll(new Label("Withdraw:"), withdrawField, withdrawBtn);

        transactionBox.getChildren().addAll(transactionLabel, depositBox, withdrawBox);

        // History section
        VBox historyBox = new VBox(10);
        historyBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 15;");
        Label historyLabel = new Label("Transaction History:");
        historyLabel.setStyle("-fx-font-weight: bold;");
        historyArea = new TextArea();
        historyArea.setEditable(false);
        historyArea.setPrefHeight(150);
        historyArea.setWrapText(true);
        Button refreshBtn = new Button("Refresh History");
        refreshBtn.setOnAction(e -> updateHistory());
        historyBox.getChildren().addAll(historyLabel, historyArea, refreshBtn);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        Button exitBtn = new Button("Exit");
        exitBtn.setStyle("-fx-padding: 10;");
        exitBtn.setOnAction(e -> System.exit(0));
        buttonBox.getChildren().add(exitBtn);

        root.getChildren().addAll(titleLabel, infoBox, transactionBox, historyBox, buttonBox);

        Scene scene = new Scene(new ScrollPane(root), 600, 700);
        primaryStage.setTitle("FinSafe - Personal Finance Manager");
        primaryStage.setScene(scene);
        primaryStage.show();

        updateHistory();
    }

    private void handleDeposit(TextField field) {
        try {
            double amount = Double.parseDouble(field.getText());
            account.deposit(amount);
            updateUI();
            field.clear();
            showAlert("Success", "Deposited: ₹" + amount);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid amount");
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void handleWithdraw(TextField field) {
        try {
            double amount = Double.parseDouble(field.getText());
            account.processTransaction(amount);
            updateUI();
            field.clear();
            showAlert("Success", "Withdrawn: ₹" + amount);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid amount");
        } catch (InSufficientFundsException e) {
            showAlert("Transaction Failed", e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void updateUI() {
        balanceLabel.setText("Balance: ₹" + String.format("%.2f", account.getBalance()));
        updateHistory();
    }

    private void updateHistory() {
        historyArea.setText(account.getMiniStatement());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
}

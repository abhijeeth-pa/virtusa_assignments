# FinSafe - Personal Finance Manager

## Overview
FinSafe is a personal financial account management application with a modern graphical interface. It helps you track deposits, withdrawals, and maintain a history of your recent transactions. The application features a clean, user-friendly interface built with JavaFX that makes managing your finances simple and intuitive.

## Features
- Create a new account with your name and initial balance
- Deposit money into your account with validation
- Withdraw money with built-in protection against overdrafts
- View your current balance in real-time
- Track your last 5 transactions
- See a mini statement with all recent activity
- Automatic error alerts for invalid transactions

## How It Works
When you launch FinSafe, you'll be greeted with a setup dialog asking for your name and opening balance. Once your account is created, the main window displays your current balance prominently and provides easy-to-use buttons for deposits and withdrawals. Every transaction is instantly reflected in your balance display and transaction history.

The transaction history shows your last five transactions, clearly marking whether each was a credit or debit. The interface prevents you from withdrawing more than your available balance and notifies you if something goes wrong.

## Technical Details
- Built with JavaFX for a modern graphical interface
- Developed in Java using object-oriented principles
- Requires Java Development Kit (JDK) 21 or later
- Uses JavaFX SDK 21.0.2 for the user interface

## Prerequisites
- Java Development Kit (JDK) 21 or later installed
- JavaFX SDK 21.0.2 downloaded and available on your system

## Installation and Setup

### Step 1: Download JavaFX
Download JavaFX SDK 21.0.2 from https://gluonhq.com/products/javafx/
Extract it to: `C:\Program Files\Java\javafx-sdk-21.0.2`

### Step 2: Compile the Application
Open your terminal in the project directory and run:

```bash
javac --module-path "C:\Program Files\Java\javafx-sdk-21.0.2\lib" --add-modules javafx.controls,javafx.fxml FinSafeGUI.java
```

### Step 3: Run the Application
```bash
java --module-path "C:\Program Files\Java\javafx-sdk-21.0.2\lib" --add-modules javafx.controls,javafx.fxml FinSafeGUI
```

## Simplified: Using Batch Scripts
To make running the application easier, create two batch files in your project folder:

**compile.bat:**
```batch
@echo off
javac --module-path "C:\Program Files\Java\javafx-sdk-21.0.2\lib" --add-modules javafx.controls,javafx.fxml FinSafeGUI.java
```

**run.bat:**
```batch
@echo off
java --module-path "C:\Program Files\Java\javafx-sdk-21.0.2\lib" --add-modules javafx.controls,javafx.fxml FinSafeGUI
```

Then you can simply run:
```bash
compile.bat
run.bat
```

## Application Interface
The main window is divided into several sections:

1. **Account Information Box** - Shows your account holder name and current balance
2. **Transaction Section** - Quick deposit and withdraw input fields with buttons
3. **History Section** - Displays your last 5 transactions in chronological order
4. **Status Messages** - Alerts notify you of successful transactions or errors

## Using the Application

### Making a Deposit
1. Enter the amount in the Deposit field
2. Click the "Deposit" button
3. You'll see a confirmation message and your balance will update instantly

### Making a Withdrawal
1. Enter the amount in the Withdraw field
2. Click the "Withdraw" button
3. If you have sufficient funds, the transaction completes and you'll see your new balance
4. If you don't have enough funds, you'll get a clear error message

### Viewing Your History
Your transaction history updates automatically after each transaction. The history shows:
- Transaction number
- Transaction type (Credit for deposits, Debit for withdrawals)
- Amount
- Current balance at that time

## Technology Behind the Application
The application demonstrates several important programming concepts:

**Object-Oriented Design** - The Account class encapsulates all financial data and operations, keeping your information secure and organized.

**Data Protection** - Account information is kept private and can only be accessed through proper methods, preventing accidental data corruption.

**Error Handling** - Custom exception handling ensures that invalid operations (like overdrafts) are caught and reported clearly.

**User Interface** - The JavaFX framework provides a modern, responsive interface that works smoothly across different systems.


## Screenshots 
![FinSafe Menu](screenshots/finsafe-menu.png)
![FinSafe Statement](screenshots/finsafe-statement.png)

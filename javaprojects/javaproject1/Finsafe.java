import java.util.ArrayList;
import java.util.Scanner;

class InSufficientFundsException extends Exception{
    public InSufficientFundsException(String msg){
        super(msg);
    }
}

class Account{
private double balance;
private String accountHolder;
private ArrayList<Double> history = new ArrayList<>();

    Account(String name, double initialBal){
        this.accountHolder = name;
        this.balance = initialBal;
    }

    public double getBalance(){ return balance; }
    public String getName(){ return accountHolder; }

    public void deposit(double amt){
        if(amt<=0) throw new IllegalArgumentException("Deposit must be positive");
        balance+=amt;
        addHistory(amt);
        System.out.println("Deposited: "+amt+" | New Balance: "+balance);
    }

    public void processTransaction(double amount) throws InSufficientFundsException{
        if(amount<0) throw new IllegalArgumentException("Amount cant be negative");
        if(amount>balance) throw new InSufficientFundsException("Not enough funds! Balance is "+balance+" but tried "+amount);
        balance-=amount;
        addHistory(-amount);
        System.out.println("Spent: "+amount+" | Remaining: "+balance);
    }

    private void addHistory(double amt){
        if(history.size()==5) history.remove(0);
        history.add(amt);
    }

    public void printMiniStatement(){
        System.out.println("--- Mini Statement for "+accountHolder+" ---");
        if(history.isEmpty()){System.out.println("No transactions yet"); return;}
        for(int i=0;i<history.size();i++){
            double t = history.get(i);
            System.out.println((i+1)+". "+(t>0?"Credit +":"Debit  ")+t);
        }
        System.out.println("Current Balance: "+balance);
    }
}

public class FinSafe{
public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    System.out.print("Enter account holder name: ");
    String name = sc.nextLine();
    System.out.print("Enter opening balance: ");
    double bal = sc.nextDouble();

    Account acc = new Account(name,bal);
    int choice=0;

    while(choice!=4){
        System.out.println("\n=== FinSafe Menu ===");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw/Spend");
        System.out.println("3. View History");
        System.out.println("4. Exit");
        System.out.print("Choice: ");
        choice = sc.nextInt();

        if(choice==1){
            System.out.print("Deposit amount: ");
            double d = sc.nextDouble();
            try{
                acc.deposit(d);
            }catch(IllegalArgumentException e){
                System.out.println("Error: "+e.getMessage());
            }
        }
        else if(choice==2){
            System.out.print("Spend amount: ");
            double w = sc.nextDouble();
            try{
                acc.processTransaction(w);
            }catch(InSufficientFundsException e){
                System.out.println("Transaction Failed - "+e.getMessage());
            }catch(IllegalArgumentException e){
                System.out.println("Invalid input - "+e.getMessage());
            }
        }
        else if(choice==3){
            acc.printMiniStatement();
        }
        else if(choice==4){
            System.out.println("Goodbye!");
        }
        else{
            System.out.println("Invalid choice, try again");
        }
    }
    sc.close();
}
}

import java.util.ArrayList;

public class Billing {
    private String memberID;
    private double totalFines = 0.0;
    private ArrayList<Double> paymentHistory = new ArrayList<>();

    Billing(String memberID){
        this.memberID = memberID;
    }

    void calculateFine(double amount){
        totalFines += amount;
        System.out.println("fine added: " + amount);
    }

    void addPayment(double payment){
        paymentHistory.add(payment);
        totalFines -= payment;
        System.out.println("payment added: " + payment);
    }

    String getPaymentStatus(){
        return "total fines: " + totalFines;
    }

    public String getMemberID() { return memberID; }
    public double getTotalFines() { return totalFines; }
}

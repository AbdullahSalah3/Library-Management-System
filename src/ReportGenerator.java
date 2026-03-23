
import java.util.ArrayList;
import java.util.Collections;

public class ReportGenerator {
    private ArrayList<String> data = new ArrayList<>();

    void addData(String item){ data.add(item); }

    // Selection Sort
    void sortData(){
        for (int i = 0; i < data.size() - 1; i++){
            int min = i;
            for (int j = i + 1; j < data.size(); j++){
                if (data.get(j).compareTo(data.get(min)) < 0) min = j;
            }
            Collections.swap(data, i, min);
        }
    }

    public String generateBookReport(){
        sortData();
        return "Book Report: " + data;
    }

    public String generateMemberReport() {
        sortData();
        return "Member Report: " + data;
    }

    public String generateBorrowingReport() {
        sortData();
        return "Borrowing Report: " + data;
    }
}

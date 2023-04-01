import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClientLog {
    private int productNum;
    private int amount;

    public void log (int productNum, int amount){
        this.productNum = productNum;
        this.amount = amount;

    }

    public void exportAsCSV(File txtFile){
        try (CSVWriter writer = new CSVWriter(new FileWriter(txtFile, true))){
            writer.writeNext(this.toString().split(","));
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return productNum + "," + amount;
    }
}
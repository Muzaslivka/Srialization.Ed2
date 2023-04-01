import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;git status
import java.util.ArrayList;
import java.util.List;

public class ClientLog {
    private List<String> logList = new ArrayList<>();


    public void log(int productNum, int amount) {
        logList.add(productNum + "," + amount);
    }

    public void exportAsCSV(File txtFile) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(txtFile))) {
            writer.writeNext(new String[]{"productNum", "amount"});
            logList.stream().map(s -> s.split(",")).forEach(writer::writeNext);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
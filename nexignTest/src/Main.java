import java.io.*;

public class Main {
    public static void main(String[] args) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("cdr.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Bad cdr file");
        }
        String line;
        CDRProcessor cdrProcessor = new CDRProcessor();
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] parts = line.split(",\\s+");
            cdrProcessor.addCall(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        for (Number number : cdrProcessor.getNumbers()) {
            cdrProcessor.writeLog(number.getNumber());
        }
    }
}

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CDRProcessor {
    private final List<Number> numbers = new ArrayList<>();

    public void addCall(String typeCalling, String number, String beginCall, String endCall, String typeTariff) {
        Number numberOperation = new Number(number, typeTariff);
        if (!numbers.contains(numberOperation)) {
            numbers.add(numberOperation);
        }
        for (Number in : numbers) {
            if (in.equals(numberOperation)) {
                in.callTo(typeCalling, beginCall, endCall);
            }
        }
    }

    public void writeLog(String number) {
        Number need = new Number(number, "2");
        for (Number in : numbers) {
            if (in.equals(need)) {
                need = in;
                break;
            }
        }
        File directory = new File("reports");
        if (!directory.exists()) {
            directory.mkdir();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter("reports\\" + number + ".txt"))) {
            writer.println("+------------------+------------------+------------------" +
                    "+------------------+------------------+");
            writer.println("| Number           | Type of tariff   | Minutes from     " +
                    "| Minutes to       | Sum of call cost |");
            writer.println("+------------------+------------------+------------------" +
                    "+------------------+------------------+");
            writer.printf("| %-16s | %-16s | %-16s | %-16s | %-16s |\n", number, need.getTypeTariff(),
                    (int) need.getTimeFrom(), (int) need.getTimeTo(), (int) need.getSum());
            writer.println("+------------------+------------------+------------------" +
                    "+------------------+------------------+");
            writer.println();
            writer.println("+---------------------+---------------------+---------------------" +
                    "+---------------------+---------------------+");
            writer.println("| Start time          | End time            | Duration            " +
                    "| Number              | Call cost           |");
            writer.println("+---------------------+---------------------+---------------------" +
                    "+---------------------+---------------------+");
            Map<List<Date>, Double> all = new LinkedHashMap<>(need.getInfoCallingTo());
            Map<List<Date>, Integer> costAllCalls = need.getCostAllCalls();
            all.putAll(need.getInfoCallingFrom());
            Map<List<Date>, Double> sortedAll = all.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().get(0))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            for (Map.Entry<List<Date>, Double> entry : sortedAll.entrySet()) {
                List<Date> dates = entry.getKey();
                Double duration = entry.getValue();
                long durationInSec = Math.round(duration * 60);
                long minutes = durationInSec / 60;
                long seconds = durationInSec % 60;
                int cost = costAllCalls.get(dates);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startTime = dateFormat.format(dates.get(0));
                String endTime = dateFormat.format(dates.get(1));
                writer.printf("| %-19s | %-19s | %02d:%02d               | %-19s | %-19s |\n",
                        startTime, endTime, minutes, seconds, number, cost);
            }
            writer.println("+---------------------+---------------------+---------------------" +
                    "+---------------------+---------------------+");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file", e);
        }
    }

    public List<Number> getNumbers() {
        return numbers;
    }
}

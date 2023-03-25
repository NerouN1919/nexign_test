import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Number {
    private final String number;
    private long timeTo = 0;
    private long timeFrom = 0;
    private final long MILLISECONDS_IN_MINUTE = 60000;
    private final String typeTariff;
    private final Map<List<Date>, Double> infoCallingTo = new HashMap<>();
    private final Map<List<Date>, Double> infoCallingFrom = new HashMap<>();
    private final Map<List<Date>, Integer> costAllCalls = new HashMap<>();
    private long sum = 0;

    public Map<List<Date>, Integer> getCostAllCalls() {
        return costAllCalls;
    }

    public Number(String number, String typeTariff) {
        this.number = number;
        this.typeTariff = typeTariff;
    }

    public void callTo(String typeCalling, String beginCall, String endCall) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date begin;
        Date end;
        try {
            begin = dateFormat.parse(beginCall);
            end = dateFormat.parse(endCall);
        } catch (ParseException e) {
            throw new RuntimeException("Bad date");
        }
        List<Date> dates = List.of(begin, end);
        Double time = ((double) end.getTime() - (double) begin.getTime()) / (double) MILLISECONDS_IN_MINUTE;
        switch (typeCalling) {
            case "01" -> {
                timeFrom += end.getTime() - begin.getTime();
                infoCallingFrom.put(dates, time);
            }
            case "02" -> {
                timeTo += end.getTime() - begin.getTime();
                ;
                infoCallingTo.put(dates, time);
            }
        }
        switch (typeTariff) {
            case "06" -> {
                if ((double) (timeTo + timeFrom) / MILLISECONDS_IN_MINUTE <= 300) {
                    sum = 100;
                    Optional<List<Date>> result = costAllCalls.entrySet().stream()
                            .filter(entry -> entry.getValue() == 100).map(Map.Entry::getKey).findFirst();
                    if (costAllCalls.size() == 0) {
                        costAllCalls.put(dates, 100);
                    } else if (result.get().get(0).compareTo(dates.get(0)) > 0) {
                        costAllCalls.put(result.get(), 0);
                        costAllCalls.put(dates, 100);
                    } else {
                        costAllCalls.put(dates, 0);
                    }
                } else {
                    int toAdd = (int) Math.ceil((
                            (double) (timeTo + timeFrom - 300 * MILLISECONDS_IN_MINUTE)) / MILLISECONDS_IN_MINUTE);
                    sum += toAdd;
                    costAllCalls.put(dates, toAdd);
                }
            }
            case "03" -> {
                int toAdd = (int) Math.ceil(((double) (timeTo + timeFrom)) / MILLISECONDS_IN_MINUTE);
                sum += toAdd;
                costAllCalls.put(dates, toAdd);
            }
            case "11" -> {
                if ((double) timeFrom / MILLISECONDS_IN_MINUTE <= 100) {
                    int toAdd = (int) Math.ceil(((double) timeFrom) / (MILLISECONDS_IN_MINUTE * 2));
                    sum += toAdd;
                    costAllCalls.put(dates, toAdd);
                } else {
                    int toAdd = (int) Math.ceil(((double) timeFrom) / MILLISECONDS_IN_MINUTE);
                    sum += toAdd;
                    costAllCalls.put(dates, toAdd);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number number1 = (Number) o;
        return Objects.equals(number, number1.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    public double getTimeTo() {
        return Math.ceil(timeTo / MILLISECONDS_IN_MINUTE);
    }

    public double getTimeFrom() {
        return Math.ceil(timeFrom / MILLISECONDS_IN_MINUTE);
    }

    public String getTypeTariff() {
        return typeTariff;
    }

    public Map<List<Date>, Double> getInfoCallingTo() {
        return infoCallingTo;
    }

    public Map<List<Date>, Double> getInfoCallingFrom() {
        return infoCallingFrom;
    }

    public double getSum() {
        return sum;
    }

    public String getNumber() {
        return number;
    }
}

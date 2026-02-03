import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Statistics {
    private int totalTraffic = 0;
    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;
    private int entryCount = 0;
    private Set<String> existingPages = new HashSet<>();
    private Map<String, Integer> osStats = new HashMap<>();

    public Statistics() {
        // Конструктор без параметров
    }

    public void addEntry(LogEntry entry) {
        // Обновление общего трафика
        totalTraffic += entry.getDataSize();
        entryCount++;

        // Обновление минимального и максимального времени
        LocalDateTime entryTime = entry.getDateTime();
        if (minTime == null || entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }
        if (maxTime == null || entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }

        // Добавление страницы с кодом ответа 200
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }

        // Обновление статистики операционных систем (с явной проверкой)
        String os = entry.getUserAgent().getOsType();

        // Проверяем, есть ли такая запись в HashMap
        if (osStats.containsKey(os)) {
            // Если есть - добавляем к соответствующему значению единицу
            int currentCount = osStats.get(os);
            osStats.put(os, currentCount + 1);
        } else {
            // Если нет - вставляем такую запись
            osStats.put(os, 1);
        }
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null || entryCount == 0) {
            return 0.0;
        }

        long hoursBetween = java.time.Duration.between(minTime, maxTime).toHours();
        if (hoursBetween == 0) {
            return totalTraffic;
        }

        return (double) totalTraffic / hoursBetween;
    }

    /**
     * Возвращает множество всех существующих страниц сайта (с кодом ответа 200)
     */
    public Set<String> getExistingPages() {
        // Возвращаем копию, чтобы защитить внутреннюю коллекцию
        return new HashSet<>(existingPages);
    }

    /**
     * Возвращает статистику операционных систем в виде долей (от 0 до 1)
     */
    public Map<String, Double> getOsStatistics() {
        Map<String, Double> result = new HashMap<>();

        if (entryCount == 0) {
            return result; // Пустая карта, если нет записей
        }

        // Общее количество записей с информацией об ОС
        int totalOsCount = 0;
        for (int count : osStats.values()) {
            totalOsCount += count;
        }

        if (totalOsCount == 0) {
            return result;
        }

        // Рассчитываем долю для каждой операционной системы
        for (Map.Entry<String, Integer> entry : osStats.entrySet()) {
            double share = (double) entry.getValue() / totalOsCount;
            result.put(entry.getKey(), share);
        }

        return result;
    }

    /**
     * Возвращает статистику операционных систем в виде количества запросов
     */
    public Map<String, Integer> getOsCounts() {
        return new HashMap<>(osStats);
    }

    // Геттеры
    public int getTotalTraffic() { return totalTraffic; }
    public LocalDateTime getMinTime() { return minTime; }
    public LocalDateTime getMaxTime() { return maxTime; }
    public int getEntryCount() { return entryCount; }

    /**
     * Возвращает количество уникальных страниц с кодом ответа 200
     */
    public int getExistingPagesCount() {
        return existingPages.size();
    }
}
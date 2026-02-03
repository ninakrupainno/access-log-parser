import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class AccessLogParser {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Путь к файлу ('exit' для выхода): ");
            String path = scanner.nextLine();
            if ("exit".equalsIgnoreCase(path)) break;

            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                System.out.println("Файл не найден или это директория");
                continue;
            }

            // Инициализация объектов статистики
            RequestStats botStats = new RequestStats();
            Statistics trafficStats = new Statistics();
            int totalLines = 0;
            int successfulParses = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Проверка длины строки
                    if (line.length() > 1024) {
                        throw new TooLongStringException("Строка >1024 символов: " + line.length());
                    }

                    totalLines++;

                    try {
                        // Создание объекта LogEntry
                        LogEntry entry = new LogEntry(line);
                        successfulParses++;

                        // Добавление в статистику трафика
                        trafficStats.addEntry(entry);

                        // Добавление в статистику ботов (используем новый метод addEntry)
                        botStats.addEntry(entry);

                    } catch (IllegalArgumentException e) {
                        // Пропускаем строки с некорректным форматом
                        System.out.println("Пропущена некорректная строка #" + totalLines);

                        // Пробуем использовать старый метод для извлечения имени бота
                        String botName = LogEntry.extractBotNameLegacy(line);
                        if (botName != null) {
                            boolean isGooglebot = "Googlebot".equalsIgnoreCase(botName);
                            boolean isYandexBot = "YandexBot".equalsIgnoreCase(botName);
                            botStats.addRequest(isGooglebot, isYandexBot);
                        }
                    }
                }

                // Вывод результатов
                System.out.println("\n" + botStats);

                // НОВАЯ СТАТИСТИКА: Существующие страницы
                System.out.println("\n=== Существующие страницы (код 200) ===");
                System.out.println("Количество уникальных страниц: " + trafficStats.getExistingPagesCount());
                if (trafficStats.getExistingPagesCount() > 0) {
                    System.out.println("Список страниц:");
                    for (String page : trafficStats.getExistingPages()) {
                        System.out.println("  - " + page);
                    }
                }

                // НОВАЯ СТАТИСТИКА: Операционные системы (доли)
                System.out.println("\n=== Статистика операционных систем (доли) ===");
                var osStats = trafficStats.getOsStatistics();
                if (osStats.isEmpty()) {
                    System.out.println("Нет данных об операционных системах");
                } else {
                    for (var entry : osStats.entrySet()) {
                        System.out.printf("  %s: %.2f%%%n", entry.getKey(), entry.getValue() * 100);
                    }
                }

                // НОВАЯ СТАТИСТИКА: Операционные системы (количества)
                System.out.println("\n=== Статистика операционных систем (количества) ===");
                var osCounts = trafficStats.getOsCounts();
                if (osCounts.isEmpty()) {
                    System.out.println("Нет данных об операционных системах");
                } else {
                    int totalOsRequests = 0;
                    for (var entry : osCounts.entrySet()) {
                        System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue());
                        totalOsRequests += entry.getValue();
                    }
                    System.out.println("Всего записей с ОС: " + totalOsRequests);
                }

                // Старая статистика трафика
                System.out.println("\n=== Статистика трафика ===");
                System.out.println("Всего строк в файле: " + totalLines);
                System.out.println("Успешно разобрано: " + successfulParses + " (" +
                        String.format("%.1f", totalLines > 0 ? (double) successfulParses / totalLines * 100 : 0) + "%)");
                System.out.println("Общий трафик: " + trafficStats.getTotalTraffic() + " байт");

                if (trafficStats.getMinTime() != null && trafficStats.getMaxTime() != null) {
                    System.out.println("Временной диапазон: " + trafficStats.getMinTime() + " - " + trafficStats.getMaxTime());
                    System.out.println("Средний трафик в час: " +
                            String.format("%.2f", trafficStats.getTrafficRate()) + " байт/час");
                } else {
                    System.out.println("Временной диапазон: не определен");
                }
                System.out.println("==========================\n");

            } catch (TooLongStringException e) {
                System.out.println("Ошибка: " + e.toString());
                break;
            } catch (Exception e) {
                System.out.println("Ошибка при чтении файла: " + e.getMessage());
            }
        }
        scanner.close();
        System.out.println("Программа завершена.");
    }
}
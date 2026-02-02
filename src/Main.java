import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
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

            RequestStats stats = new RequestStats();

            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.length() > 1024) throw new TooLongStringException("Строка >1024 символов: " + line.length());

                    String botName = extractBotName(line);
                    boolean isGooglebot = "Googlebot".equalsIgnoreCase(botName);
                    boolean isYandexBot = "YandexBot".equalsIgnoreCase(botName);
                    stats.addRequest(isGooglebot, isYandexBot);
                }
                System.out.println("\n" + stats + "\n");
            } catch (TooLongStringException e) {
                System.out.println("Ошибка: " + e.toString()); break;
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static String extractBotName(String line) {
        int lastQuote = line.lastIndexOf("\"");
        int secondLastQuote = line.lastIndexOf("\"", lastQuote - 1);
        if (secondLastQuote == -1 || lastQuote == -1) return null;

        String userAgent = line.substring(secondLastQuote + 1, lastQuote);
        int open = userAgent.indexOf('('), close = userAgent.indexOf(')', open);
        if (open == -1 || close == -1) return null;

        String[] parts = userAgent.substring(open + 1, close).split(";");
        if (parts.length < 2) return null;

        String fragment = parts[1].trim();
        int slash = fragment.indexOf('/');
        return slash != -1 ? fragment.substring(0, slash).trim() : fragment.trim();
    }
}
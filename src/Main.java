import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int count = 0;
        while (true) {
            int totalLines = 0;
            int maxLength = 0;
            int minLength = Integer.MAX_VALUE; // Начинаем с максимального значения

            String path = new Scanner(System.in).nextLine();
            File file = new File(path);
            boolean fileExist = file.exists();
            boolean isDirectory = file.isDirectory();

            if ((!fileExist) || (isDirectory)) {
                System.out.println("Указанный файл не существует или это просто папка");
                continue;
            } else {
                count++;
                System.out.println("Путь указан верно");
                System.out.println("Это файл номер " + count);

                try {
                    FileReader fileReader = new FileReader(path);
                    BufferedReader reader = new BufferedReader(fileReader);
                    String line;

                    // Считываем файл построчно
                    while ((line = reader.readLine()) != null) {
                        int length = line.length();

                        // Проверяем длину строки
                        if (length > 1024) {
                            throw new TooLongStringException(
                                    "Обнаружена строка длиной " + length +
                                            " символов. Максимально допустимая длина: 1024 символа."
                            );
                        }

                        // Обновляем статистику
                        totalLines++;
                        if (length > maxLength) {
                            maxLength = length;
                        }
                        if (length < minLength) {
                            minLength = length;
                        }
                    }

                    // Закрываем ресурсы
                    reader.close();
                    fileReader.close();

                    // Проверяем, был ли файл пустым
                    if (totalLines == 0) {
                        minLength = 0; // Если файл пустой, то самая короткая строка имеет длину 0
                    }

                    // Выводим статистику
                    System.out.println("=== Статистика файла ===");
                    System.out.println("Общее количество строк: " + totalLines);
                    System.out.println("Длина самой длинной строки: " + maxLength);
                    System.out.println("Длина самой короткой строки: " + minLength);
                    System.out.println("========================\n");

                } catch (TooLongStringException e) {
                    // Обрабатываем наше исключение
                    System.out.println("Ошибка: " + e.getMessage());
                    break; // Прерываем выполнение программы
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
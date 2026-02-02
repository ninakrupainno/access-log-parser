import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

class LogEntry {
    private final String ipAddress;
    private final LocalDateTime dateTime;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int dataSize;
    private final String referer;
    private final UserAgent userAgent;
    private final String originalLine;

    // Форматтер для даты в логах
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("dd/MMM/yyyy:HH:mm:ss")
            .appendLiteral(" ")
            .appendOffset("+HHMM", "+0000")
            .toFormatter(Locale.ENGLISH);

    public LogEntry(String logLine) {
        this.originalLine = logLine;
        try {
            // Разбор строки лога
            String[] parts = logLine.split("\"");

            if (parts.length < 5) {
                throw new IllegalArgumentException("Недостаточно частей в строке лога");
            }

            // Часть до первых кавычек: IP, дата и время
            String firstPart = parts[0].trim();
            String[] firstPartSplit = firstPart.split(" ");
            this.ipAddress = firstPartSplit[0];

            // Извлечение даты и времени
            int dateStart = firstPart.indexOf("[");
            int dateEnd = firstPart.indexOf("]");
            if (dateStart == -1 || dateEnd == -1) {
                throw new IllegalArgumentException("Отсутствует дата в формате [дата]");
            }
            String dateStr = firstPart.substring(dateStart + 1, dateEnd);
            this.dateTime = LocalDateTime.parse(dateStr, DATE_FORMATTER);

            // Метод и путь
            String requestPart = parts[1];
            String[] requestParts = requestPart.split(" ");
            if (requestParts.length < 2) {
                throw new IllegalArgumentException("Некорректный формат запроса");
            }
            try {
                this.method = HttpMethod.valueOf(requestParts[0]);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Неизвестный метод HTTP: " + requestParts[0]);
            }
            this.path = requestParts[1];

            // Код ответа и размер данных
            String statusAndSize = parts[2].trim();
            String[] statusParts = statusAndSize.split(" ");
            if (statusParts.length < 2) {
                throw new IllegalArgumentException("Некорректный формат кода ответа и размера данных");
            }
            this.responseCode = Integer.parseInt(statusParts[0]);
            this.dataSize = Integer.parseInt(statusParts[1]);

            // Referer
            this.referer = parts[3].isEmpty() || parts[3].equals("-") ? "" : parts[3];

            // User-Agent
            String userAgentStr = parts.length > 4 ? parts[5] : "";
            if (userAgentStr.isEmpty() || userAgentStr.equals("-")) {
                userAgentStr = "Unknown";
            }
            this.userAgent = new UserAgent(userAgentStr);

        } catch (Exception e) {
            throw new IllegalArgumentException("Неверный формат строки лога: " + e.getMessage(), e);
        }
    }

    // Статический метод для извлечения имени бота (старый метод для обратной совместимости)
    public static String extractBotNameLegacy(String line) {
        try {
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
        } catch (Exception e) {
            return null;
        }
    }

    // Геттеры
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getDateTime() { return dateTime; }
    public HttpMethod getMethod() { return method; }
    public String getPath() { return path; }
    public int getResponseCode() { return responseCode; }
    public int getDataSize() { return dataSize; }
    public String getReferer() { return referer; }
    public UserAgent getUserAgent() { return userAgent; }
    public String getOriginalLine() { return originalLine; }
}
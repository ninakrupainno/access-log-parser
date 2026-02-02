import java.util.Objects;

class RequestStats {
    private int total = 0;
    private int googlebot = 0;
    private int yandexBot = 0;

    public void addEntry(LogEntry entry) {
        total++;
        UserAgent ua = entry.getUserAgent();

        // Используем методы isGooglebot() и isYandexBot() из UserAgent
        if (ua.isGooglebot()) {
            googlebot++;
        }
        if (ua.isYandexBot()) {
            yandexBot++;
        }
    }

    public void addRequest(boolean isGooglebot, boolean isYandexBot) {
        total++;
        if (isGooglebot) googlebot++;
        if (isYandexBot) yandexBot++;
    }

    @Override public String toString() {
        double googlebotPercent = total > 0 ? (double) googlebot / total * 100 : 0;
        double yandexBotPercent = total > 0 ? (double) yandexBot / total * 100 : 0;

        return String.format("=== Статистика ботов ===\n" +
                        "Всего запросов: %d\n" +
                        "Googlebot: %d (%.2f%%)\n" +
                        "YandexBot: %d (%.2f%%)\n" +
                        "=========================",
                total,
                googlebot, googlebotPercent,
                yandexBot, yandexBotPercent);
    }
}
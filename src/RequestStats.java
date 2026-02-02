import java.util.Objects;

class RequestStats {
    private int total, googlebot, yandexBot;

    public void addRequest(boolean isGooglebot, boolean isYandexBot) {
        total++;
        if (isGooglebot) googlebot++;
        if (isYandexBot) yandexBot++;
    }

    @Override public String toString() {
        return String.format("=== Результаты анализа ===\nВсего: %d\nGooglebot: %d (%.2f%%)\nYandexBot: %d (%.2f%%)\n=========================",
                total, googlebot, total > 0 ? (double) googlebot / total * 100 : 0,
                yandexBot, total > 0 ? (double) yandexBot / total * 100 : 0);
    }

    @Override public int hashCode() { return Objects.hash(total, googlebot, yandexBot); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestStats other = (RequestStats) o;
        return total == other.total && googlebot == other.googlebot && yandexBot == other.yandexBot;
    }
}
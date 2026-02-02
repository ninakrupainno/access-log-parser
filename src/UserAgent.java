class UserAgent {
    private final String osType;    // Тип ОС
    private final String browser;   // Браузер
    private final String originalUserAgent; // Оригинальная строка User-Agent

    public UserAgent(String userAgentString) {
        this.originalUserAgent = userAgentString;
        this.osType = parseOsType(userAgentString);
        this.browser = parseBrowser(userAgentString);
    }

    private String parseOsType(String userAgent) {
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) return "Windows";
        if (ua.contains("mac os") || ua.contains("macos")) return "macOS";
        if (ua.contains("linux")) return "Linux";
        if (ua.contains("android")) return "Android";
        if (ua.contains("ios") || ua.contains("iphone")) return "iOS";
        return "Other";
    }

    private String parseBrowser(String userAgent) {
        String ua = userAgent.toLowerCase();

        // Сначала проверяем на популярные браузеры
        if (ua.contains("edge")) return "Edge";
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("chrome") && !ua.contains("chromium")) return "Chrome";
        if (ua.contains("safari") && !ua.contains("chrome")) return "Safari";
        if (ua.contains("opera")) return "Opera";

        // Затем проверяем на ботов
        if (ua.contains("googlebot")) return "Googlebot";
        if (ua.contains("yandexbot") || ua.contains("yandexbot/")) return "YandexBot";
        if (ua.contains("bingbot")) return "Bingbot";
        if (ua.contains("facebookexternalhit")) return "FacebookBot";
        if (ua.contains("twitterbot")) return "TwitterBot";

        return "Other";
    }

    // Методы для проверки ботов
    public boolean isGooglebot() {
        String browser = this.browser.toLowerCase();
        String original = this.originalUserAgent.toLowerCase();
        return browser.contains("googlebot") || original.contains("googlebot");
    }

    public boolean isYandexBot() {
        String browser = this.browser.toLowerCase();
        String original = this.originalUserAgent.toLowerCase();
        return browser.contains("yandexbot") || original.contains("yandexbot");
    }

    // Геттеры
    public String getOsType() { return osType; }
    public String getBrowser() { return browser; }
    public String getOriginalUserAgent() { return originalUserAgent; }
}
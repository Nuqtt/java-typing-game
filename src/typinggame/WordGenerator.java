package typinggame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * WordGenerator - 強化版
 * * 進階功能：
 * 具備「雙重路徑搜尋」機制，自動適應 IDE 與 CMD 環境。
 */
public class WordGenerator {
    
    private static final String[] FALLBACK_WORDS = {
        "cat", "dog", "java", "code", "game", "jump", "run", "sky", "blue", 
        "apple", "music", "write", "learn", "happy", "smile", "world"
    };

    private final List<String> words;
    private final Random random = new Random();

    public WordGenerator() {
        this.words = new ArrayList<>();
        loadDictionary();
    }

    /** 嘗試載入 dictionary.json */
    private void loadDictionary() {
        try {
            InputStream is = null;
            String loadSource = "";

            // 1. 第一招：嘗試從 Classpath (bin 資料夾) 讀取 (標準發布模式)
            is = getClass().getResourceAsStream("/typinggame/assets/dictionary.json");
            if (is != null) {
                loadSource = "Classpath (bin)";
            }

            // 2. 第二招：如果第一招失敗，嘗試直接讀取 src 資料夾 (VS Code 開發模式)
            if (is == null) {
                // 這裡指定 src 的相對路徑
                File devFile = new File("src/typinggame/assets/dictionary.json");
                if (devFile.exists()) {
                    is = new FileInputStream(devFile);
                    loadSource = "Source Folder (src)";
                } else {
                    // 印出目前的工作目錄，方便除錯
                    System.err.println("Debugging info: Current working dir = " + System.getProperty("user.dir"));
                }
            }
            
            // 3. 如果兩招都失敗
            if (is == null) {
                System.err.println("❌ Error: Dictionary file NOT found in bin or src.");
                useFallback();
                return;
            }

            // 讀取並解析
            System.out.println("✅ Dictionary found in: " + loadSource); // 顯示成功訊息
            String jsonContent = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            parseJsonAndAddWords(jsonContent);

            System.out.println("Loaded " + words.size() + " words from dictionary.");
            
            if (words.isEmpty()) {
                useFallback();
            }

        } catch (Exception e) {
            System.err.println("Error loading dictionary: " + e.getMessage());
            e.printStackTrace();
            useFallback();
        }
    }

    private void parseJsonAndAddWords(String json) {
        int arrayStart = json.indexOf('[');
        int arrayEnd = json.lastIndexOf(']');

        if (arrayStart >= 0 && arrayEnd > arrayStart) {
            String arrayContent = json.substring(arrayStart + 1, arrayEnd);
            String[] rawTokens = arrayContent.split(",");

            for (String token : rawTokens) {
                String word = token.trim().replaceAll("[\"']", "").trim();
                if (!word.isEmpty() && word.matches("[a-zA-Z]+")) {
                    words.add(word.toLowerCase());
                }
            }
        }
    }

    private void useFallback() {
        System.out.println("⚠️ Using Fallback Words (Basic List)");
        for (String w : FALLBACK_WORDS) {
            words.add(w);
        }
    }

    public String next() {
        if (words.isEmpty()) return "error";
        return words.get(random.nextInt(words.size()));
    }

    public String next(int difficultyLevel) {
        int minLen = Math.min(3 + difficultyLevel, 10);
        int maxLen = Math.min(minLen + 4, 15);

        String candidate = null;
        for (int i = 0; i < 20; i++) {
            String w = words.get(random.nextInt(words.size()));
            if (w.length() >= minLen && w.length() <= maxLen) {
                candidate = w;
                break;
            }
        }
        return (candidate != null) ? candidate : next();
    }
}
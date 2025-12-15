package typinggame;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * WordGenerator - 支援讀取外部 JSON 字典檔
 * * 進階物件導向設計：
 * 1. 資源讀取 (IO) 與 遊戲邏輯 分離。
 * 2. 具備 容錯機制 (Fallback)，讀檔失敗時不會讓遊戲崩潰。
 * 3. 簡單的 JSON 解析器 (不依賴第三方庫)。
 */
public class WordGenerator {
    
    // 備用單字庫 (當讀取外部檔案失敗時使用)
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
            // 讀取 src/typinggame/assets/dictionary.json
            InputStream is = getClass().getResourceAsStream("/typinggame/assets/dictionary.json");
            
            if (is == null) {
                System.err.println("Dictionary file not found, using fallback words.");
                useFallback();
                return;
            }

            // 讀取檔案內容轉為 String
            String jsonContent = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            // 解析 JSON
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

    /**
     * 一個簡易的 JSON 解析器 (針對 "common.json" 或 字串陣列格式)
     * 支援格式: {"commonWords": ["a", "b", ...]} 或 ["a", "b", ...]
     */
    private void parseJsonAndAddWords(String json) {
        // 1. 去除 JSON 的大括號、中括號與鍵名，只留下內容
        // 這種暴力法對於簡單的單字清單非常有效且快速
        
        // 移除 JSON 物件的大括號和 key (針對 common.json 的結構)
        // 如果是純陣列 ["a","b"] 也不會受影響
        int arrayStart = json.indexOf('[');
        int arrayEnd = json.lastIndexOf(']');

        if (arrayStart >= 0 && arrayEnd > arrayStart) {
            String arrayContent = json.substring(arrayStart + 1, arrayEnd);
            
            // 2. 根據逗號分割
            String[] rawTokens = arrayContent.split(",");

            for (String token : rawTokens) {
                // 3. 去除引號與空白
                String word = token.trim().replaceAll("[\"']", "").trim();
                
                // 4. 簡單過濾：只收錄純英文字母的單字
                if (!word.isEmpty() && word.matches("[a-zA-Z]+")) {
                    words.add(word.toLowerCase());
                }
            }
        }
    }

    private void useFallback() {
        for (String w : FALLBACK_WORDS) {
            words.add(w);
        }
    }

    /** 隨機取得一個單字 */
    public String next() {
        if (words.isEmpty()) return "error";
        return words.get(random.nextInt(words.size()));
    }

    /**
     * 根據難度取得單字
     * difficultyLevel 越高，傾向出現越長的單字
     */
    public String next(int difficultyLevel) {
        // 設定長度範圍
        int minLen = Math.min(3 + difficultyLevel, 10);
        int maxLen = Math.min(minLen + 4, 15); // 稍微放寬上限

        // 從載入的字典中篩選符合長度的候選字
        // (為了效能，這裡不做全列表過濾，而是隨機嘗試幾次)
        
        String candidate = null;
        // 嘗試找 20 次，如果都找不到符合長度的，就直接回傳隨機字
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
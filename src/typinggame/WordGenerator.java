package typinggame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Supplies random words for obstacles.
 */
public class WordGenerator {
    private static final String[] WORDS = {
            "cat", "dog", "apple", "banana", "orange", "hello", "world", "java", "swing",
            "keyboard", "mouse", "laptop", "screen", "window", "game", "play", "jump", "slide",
            "tree", "river", "mountain", "sky", "earth", "ocean", "sun", "moon", "star", "space",
            "school", "student", "teacher", "coding", "typing", "random", "object", "class", "method",
            "variable", "function", "string", "integer", "double", "boolean", "public", "private",
            "static", "final", "import", "package", "system", "exception", "thread", "array", "list",
            "queue", "stack", "loop", "condition", "switch", "case", "break", "continue", "return",
            "assign", "declare", "compile", "execute", "debug", "test", "console", "input", "output",
            "file", "network", "internet", "server", "client", "protocol", "socket", "browser",
            "chrome", "firefox", "edge", "opera", "safari", "android", "iphone", "tablet", "desktop",
            "lorem", "ipsum", "dolor", "sit", "amet"
    };

    private final Random random = new Random();

    /** 舊版：不考慮難度，純隨機一個字。 */
    public String next() {
        return WORDS[random.nextInt(WORDS.length)];
    }

    /**
     * 新版：根據 difficultyLevel 粗略控制單字長度，讓遊戲後期出現較長的單字。
     */
    public String next(int difficultyLevel) {
        int minLen = Math.min(3 + difficultyLevel, 10);
        int maxLen = Math.min(minLen + 4, 12);

        List<String> candidates = new ArrayList<>();
        for (String w : WORDS) {
            int len = w.length();
            if (len >= minLen && len <= maxLen) {
                candidates.add(w);
            }
        }

        if (!candidates.isEmpty()) {
            return candidates.get(random.nextInt(candidates.size()));
        }
        // 若沒有符合條件的，就退回原本隨機
        return next();
    }
}

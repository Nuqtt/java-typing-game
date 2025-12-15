package typinggame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryLeaderboard implements LeaderboardStorage {
    private final List<ScoreEntry> scores = new ArrayList<>();

    @Override
    public void saveScore(ScoreEntry entry) {
        scores.add(entry);
        Collections.sort(scores);
    }

    @Override
    public List<ScoreEntry> getTopScores(int limit) {
        int actualLimit = Math.min(limit, scores.size());
        return new ArrayList<>(scores.subList(0, actualLimit));
    }
}
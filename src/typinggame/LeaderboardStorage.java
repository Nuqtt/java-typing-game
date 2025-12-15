package typinggame;

import java.util.List;

public interface LeaderboardStorage {
    void saveScore(ScoreEntry entry);
    List<ScoreEntry> getTopScores(int limit);
}
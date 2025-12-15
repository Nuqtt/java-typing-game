package typinggame;

import java.util.Date;

public class ScoreEntry implements Comparable<ScoreEntry> {
    private String playerName;
    private long score;
    private Date date;

    public ScoreEntry(String playerName, long score) {
        this.playerName = playerName;
        this.score = score;
        this.date = new Date();
    }

    // Getters
    public String getPlayerName() { return playerName; }
    public long getScore() { return score; }
    public Date getDate() { return date; }

    @Override
    public int compareTo(ScoreEntry other) {
        // 分數高的排前面
        return Long.compare(other.score, this.score);
    }
}
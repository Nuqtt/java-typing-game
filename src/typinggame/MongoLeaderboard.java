package typinggame;

import com.mongodb.client.*;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class MongoLeaderboard implements LeaderboardStorage {
    private final MongoCollection<Document> collection;

    public MongoLeaderboard(String connectionString, String dbName, String colName) {
        // 建立連線 (建議使用 try-with-resources 管理 client，但在這個簡單範例中我們先保持連線)
        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase db = client.getDatabase(dbName);
        this.collection = db.getCollection(colName);
    }

    @Override
    public void saveScore(ScoreEntry entry) {
        Document doc = new Document("name", entry.getPlayerName())
                .append("score", entry.getScore())
                .append("date", entry.getDate());
        collection.insertOne(doc);
    }

    @Override
    public List<ScoreEntry> getTopScores(int limit) {
        List<ScoreEntry> list = new ArrayList<>();
        // 查詢：依照 score 遞減排序 (descending)，取前 limit 筆
        FindIterable<Document> docs = collection.find()
                .sort(new Document("score", -1))
                .limit(limit);

        for (Document d : docs) {
            String name = d.getString("name");
            // MongoDB 數字可能會存成 Integer 或 Long，安全轉型
            long score = d.get("score") instanceof Number ? ((Number) d.get("score")).longValue() : 0;
            list.add(new ScoreEntry(name, score));
        }
        return list;
    }
}
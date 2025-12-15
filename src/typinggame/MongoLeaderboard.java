package typinggame;

import com.mongodb.client.*;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class MongoLeaderboard implements LeaderboardStorage {
    private final MongoCollection<Document> collection;

    // 建構子：建立連線
    public MongoLeaderboard(String connectionString, String dbName, String colName) {
        // 連接本地 MongoDB (預設 port 27017)
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
        // 依照分數(score) 降序(-1) 排列
        FindIterable<Document> docs = collection.find()
                .sort(new Document("score", -1))
                .limit(limit);

        for (Document d : docs) {
            String name = d.getString("name");
            // 處理數字型別轉換 (MongoDB 預設可能是 Integer 或 Long)
            Number num = (Number) d.get("score");
            long score = (num != null) ? num.longValue() : 0;
            list.add(new ScoreEntry(name, score));
        }
        return list;
    }
}
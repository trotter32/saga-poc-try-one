package io.github.sagapoctryone;

import com.mongodb.client.*;
import org. bson. Document;

public class TransactionTest {
    public static void main(String[] args) {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db = client.getDatabase("testDB");

        ClientSession session = client.startSession();

        try {
            session.startTransaction();

            MongoCollection<Document> coll1 = db.getCollection("coll1");
            MongoCollection<Document> coll2 = db.getCollection("coll2");

            coll1.insertOne(session, new Document("_id", "67f302e0f9293b09138c5cea"));
            coll2.insertOne(session, new Document("key", "value1"));

            session.commitTransaction();
            System.out.println("Transaction committed successfully");
        } catch (Exception e) {
            session.abortTransaction();
            System.out.println("Transaction aborted: " + e.getMessage());
        } finally {
            session.close();
        }
    }
}
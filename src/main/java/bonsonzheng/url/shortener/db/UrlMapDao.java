package bonsonzheng.url.shortener.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;

import java.util.Iterator;


public class UrlMapDao {

    private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private static final DynamoDB dynamoDB = new DynamoDB(client);
    private static final String tableName = "url_map";


    public String putItemIfNotExists(String longUrl, String shortUrl) throws Exception {

        try {
            Table table = dynamoDB.getTable(tableName);

            Item item = new Item().withString("longUrl", longUrl).withString("shortUrl", shortUrl);

            PutItemSpec putItemSpec = new PutItemSpec().withItem(item).withConditionExpression("attribute_not_exists(longUrl)");

            table.putItem(putItemSpec);

            return shortUrl;
        } catch (ConditionalCheckFailedException conditionalCheckFailedException) {
            return getShortUrl(longUrl);
        }

    }

    public  String getShortUrl(String longUrl) throws Exception {
        Table table = dynamoDB.getTable(tableName);

        Item item = table.getItem("longUrl", longUrl, "shortUrl", null);
        return item.getString("shortUrl");

    }

    public String retrieveItem(String shortUrl) {
        Table table = dynamoDB.getTable(tableName);


        Index index = table.getIndex("shortUrl-index");

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("shortUrl= :v_shortUrl")
                .withValueMap(new ValueMap()
                        .withString(":v_shortUrl", shortUrl));


        ItemCollection<QueryOutcome> items = index.query(spec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            String s = iter.next().toJSONPretty();
            return s;
        }

        return null;
    }
}

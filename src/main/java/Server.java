import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Server {
    Cluster cluster;
    Session session;
    final static String yafi = "yafi";
    final static String[] contactPoints  = {"167.205.35.19"};

    public void register(User user){
        execute("INSERT INTO users (username,password) VALUES ('" + user.getUsername() + "','" + user.getPassword() + "')");
    }

    public void friend(String current,String target){
        execute("INSERT INTO friends (username,friend) VALUES ('" + current + "','" + target + "')");
    }

    public void follow(String follower,String followed){
        execute("INSERT INTO followers (username,follower,since) VALUES ('" + followed + "','" + follower + "',dateof(now()))");
    }

    public void tweet (String username, String body) {
        // Create the tweet in the tweets cf
        UUID id = UUIDs.timeBased();
        execute("INSERT INTO tweets (tweet_id, username, body) VALUES (%s, '%s', '%s')",
                id.toString(),
                username,
                body);
        // Store the tweet in this users userline
        execute("INSERT INTO userline (username,time,tweet_id) VALUES ('%s',%s,%s)",
                username,
                id.toString(),
                id.toString());
        // Store the tweet in this users timeline
        execute("INSERT INTO timeline (username,time, tweet_id) VALUES ('%s', %s, %s)",
                username,
                id.toString(),
                id.toString());

        // Insert the tweet into follower timelines
        for (String follower : getFollowers(username)) {
            execute("INSERT INTO timeline (username,time, tweet_id) VALUES ('%s',%s, %s)",
                    follower,
                    id.toString(),
                    id.toString());
        }

    }

    public List<String> getFollowers(String username) {
        ResultSet queryResult = execute("SELECT * FROM followers WHERE username = '%s'", username);
        List<String> followers = new ArrayList<String>();

        for (Row row : queryResult)
            followers.add(row.getString("follower"));

        return followers;
    }

    public List<Tweet> getTweets(String username){
        List<Tweet> tweets = new ArrayList<>();
        ResultSet execute = execute("SELECT * FROM tweets WHERE username = '" + username + "'");
        for(Row row:execute){
            Tweet t = new Tweet();
            t.setTweetId(row.getUUID("tweet_id").toString());
            t.setUsername(row.getString("username"));
            t.setBody(row.getString("body"));
        }
        return tweets;
    }

    public List<Tweet> getUserline(String username){
        List<Tweet> tweets = new ArrayList<>();
        ResultSet execute = execute("SELECT * FROM userline WHERE username='" + username + "' ORDER BY time DESC");
        for (Row row:execute){
            tweets.add(getTweet(row.getUUID("tweet_id").toString()));
        }
        return tweets;
    }

    public List<Tweet> getTimeline(String username){
        List<Tweet> tweets = new ArrayList<>();
        ResultSet execute = execute("SELECT * FROM timeline WHERE username='" + username + "' ORDER BY time DESC");
        for (Row row:execute){
            tweets.add(getTweet(row.getUUID("tweet_id").toString()));
        }
        return tweets;
    }

    public Tweet getTweet(String id){
        ResultSet execute = execute("SELECT * FROM tweets WHERE tweet_id=%s LIMIT 1", id);
        if (execute == null){
            return null;
        } else {
            Tweet t = new Tweet();
            Row row = execute.one();
            t.setUsername(row.getString("username"));
            t.setTweetId(row.getUUID("tweet_id").toString());
            t.setBody(row.getString("body"));
            return t;
        }
    }

    public boolean login(User user){
        String query = String.format("SELECT * FROM users WHERE username='%s' LIMIT 1",user.getUsername(), user.getPassword());
        ResultSet execute = execute(query);
        List<Row> all = execute.all();
        if (all.isEmpty()) {
            return false;
        } else {
            if (!all.get(0).getString("password").equals(user.getPassword())){
                return false;
            } else
            return true;
        }
    }

    private ResultSet execute(String query, Object...parms) {
        String cql = String.format(query, parms);
        System.err.println("Executing CQL: {}"+ cql);
        return session.execute(cql);
    }

    public Server(){
        Cluster.Builder builder = Cluster.builder();
        for(String cp : contactPoints){
            builder.addContactPoint(cp);
        }
        cluster = builder.build();
        session = cluster.connect(yafi);
    }
}

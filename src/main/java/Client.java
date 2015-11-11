import java.util.List;
import java.util.Scanner;

public class Client {
//    - follow a friend: insert row ke tabel friends dan followers
//    - tweet: insert row ke tabel tweet, userline, timeline dan timeline semua follower
//    - menampilkan tweet per user
//    - menampilkan timeline per user
    public static void main(String[] args){
        boolean stop = false;
        Server server = new Server();
        User uSession = null;
        Scanner sc = new Scanner(System.in);
        do {
            System.out.println("Available commands:");
            System.out.println("register <username> <password> : register the username and password to database");
            System.out.println("login <username> <password> : login with the given username and password");
            System.out.println("follow <username> : current user will follow <username>");
            System.out.println("userline <username> : get userline of username");
            System.out.println("timeline <username> : get timeline of username");
            System.out.println("tweet <body> : do tweet");
            System.out.println("exit\n");
            System.out.print(":command >");
            String input = sc.nextLine();
            String[] split = input.split("\\s+");

            if (split[0].equalsIgnoreCase("register")){
                if (split.length != 3){
                    System.out.println("[ERROR] Usage: register <username> <password>");
                } else {
                    User user = new User(); user.setUsername(split[1]); user.setPassword(split[2]);
                    server.register(user);
                    System.out.println("[SUCCESS] register success");
                }
            } else if (split[0].equalsIgnoreCase("login")){
                User user = new User(); user.setUsername(split[1]); user.setPassword(split[2]);
                if (split.length != 3){
                    System.out.println("[ERROR] Usage: login <username> <password>");
                }
                else if (server.login(user)){
                    uSession = user;
                    System.out.println("[SUCCESS] Login successful as "+uSession.getUsername());
                } else {
                    System.out.println("[ERROR] Username/password not match");
                }
            } else if (split[0].equalsIgnoreCase("follow")){
                if (split.length != 2){
                    System.out.println("[ERROR] Usage: follow <username>");
                }
                else //noinspection ConstantConditions
                    if (uSession == null){
                    System.out.println("[ERROR] You are not logged in");
                } else {
                    server.follow(uSession.getUsername(),split[1]);
                    System.out.println("[SUCCESS] you followed "+split[1]);
                }
            } else if (split[0].equalsIgnoreCase("userline")){
                if (split.length != 2){
                    System.out.println("[ERROR] Usage: userline <username>");
                } else {
                    List<Tweet> userline = server.getUserline(split[1]);
                    for(Tweet t: userline){
                        System.out.println("["+t.getUsername()+"] "+t.getBody());
                    }
                }
            } else if (split[0].equalsIgnoreCase("timeline")){
                if (split.length != 2){
                    System.out.println("[ERROR] Usage: timeline <username>");
                } else {
                    List<Tweet> timeline = server.getTimeline(split[1]);
                    for(Tweet t: timeline){
                        System.out.println("["+t.getUsername()+"] "+t.getBody());
                    }
                }
            } else if (split[0].equalsIgnoreCase("tweet")){
                String tweet = new String();
                for(int i = 1; i < split.length; i++){
                    tweet += " " + split[i];
                }
                server.tweet(uSession.getUsername(),tweet);
                System.out.println("[SUCCESS] Tweet posted");
            } else if (split[0].equalsIgnoreCase("exit")){
                System.out.println("[SUCCESS] bye");
                stop = true;
            } else {
                System.out.println("[ERROR] Unknown command");
            }
        } while (!stop);

    }
}

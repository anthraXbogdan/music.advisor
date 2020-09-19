import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    static LinkedHashMap<String, String> categoryIDs = new LinkedHashMap<>();
    public static ArrayList<String> viewer = new ArrayList<>();
    static boolean getAccess = false;
    static HttpServer server;
    static String query = "";
    static String accessCode = "";
    static String spotifyAccessServer = "https://accounts.spotify.com";
    static String spotifyResourceServer = "https://api.spotify.com";
    static String token;
    static String categoryPlaylist;
    static String action2;
    static int entriesPerPage = 5;

    static int entryNo = 0;
    static int rightBound = entriesPerPage;
    static double totalPages;
    static int page = 1;
    static int totalPages2;

    static void accessMessage() {
        System.out.println("Please, provide access for application.");
    }

    static void reset() {
        Requests.viewer.clear();
        entryNo = 0;
        rightBound = entriesPerPage;
        page = 1;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length > 1) {
            for (int i = 0; i < args.length - 1; i++) {
                switch (args[i]) {
                    case "-access":
                        spotifyAccessServer = args[i + 1];
                        break;
                    case "-resource":
                        spotifyResourceServer = args[i + 1];
                        break;
                    case "-page":
                        entriesPerPage = Integer.parseInt(args[i + 1]);
                        break;
                }
            }
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please type in one of the following actions: auth, new, featured, categories, playlists CATEGORY_NAME, exit:");

        while (scanner.hasNext()) {
            String action = scanner.nextLine();


            if (action.contains(" ")) {
                action2 = action.substring(0, action.indexOf(' '));
                categoryPlaylist = action.substring(action.indexOf(" ") + 1);
                System.out.println(categoryPlaylist);
            } else {
                action2 = action;
            }

            switch (action2) {
                //Starts the local server and make a request to Spotify server for an access token
                case "auth":
                    System.out.println("use this link to request the access code:");
                    System.out.println("https://accounts.spotify.com/authorize?client_" +
                            "id=a0bb9c0b0b5943e1b37a22849de08792&" +
                            "redirect_uri=http://localhost:8080&response_type=code");
                    System.out.println("\nwaiting for code...");
                    Server.createServer();
                    server.start();
                    Server.createHandler();

                    //delays execution of the next line of code - in seconds -
                    try {
                        TimeUnit.SECONDS.sleep(7);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("code received");
                    Requests.accessTokenRequest();
                    server.stop(0);

                    System.out.println("Success!");
                    getAccess = true;
                    break;

                case "new":
                    if (getAccess) {
                        reset();
                        Requests.newReleasesRequest();
                        Viewer.playlistViewer();
                    } else {
                        accessMessage();
                    }
                    break;

                case "featured":
                    if (getAccess) {
                        reset();
                        Requests.featuredRequests();
                        Viewer.playlistViewer();
                    } else {
                        accessMessage();
                    }
                    break;

                case "categories":
                    if (getAccess) {
                        reset();
                        Requests.categoriesRequest();
                        Viewer.playlistViewer();
                    } else {
                        accessMessage();
                    }
                    break;

                case "playlists" :
                    if (getAccess) {
                        if (categoryIDs.containsKey(categoryPlaylist)) {
                            reset();
                            Requests.playlistRequests();
                            Viewer.playlistViewer();
                        } else {
                            System.out.println("Unknown category name.");
                            System.out.println();
                        }
                    } else {
                        accessMessage();
                    }
                    break;

                case "next":
                    Viewer.nextPage();
                    break;

                case "prev":
                    Viewer.prevPage();
                    break;

                case "exit":
                    //System.out.println("---GOODBYE!---");
                    System.exit(0);

                default:
                    break;
            }
        }
    }

}


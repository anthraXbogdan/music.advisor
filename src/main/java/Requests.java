import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

//Make different request to the Spotify server
public class Requests extends Main {

    static String accessToken;

    public static void accessTokenRequest() throws IOException, InterruptedException {

        System.out.println("Making http request for access_token...");

        String parameters = "grant_type=authorization_code&code=" + accessCode + "&redirect_uri=http://localhost:8080";

        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Basic YTBiYjljMGIwYjU5NDNlMWIzN2EyMjg0OWRlMDg3OTI6OWQzNjk3ZDgxYzhjNGUyZjk4NDhhZWU2MDI5NTIwMDI")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(spotifyAccessServer + "/api/token"))
                .timeout(Duration.ofMinutes(2))
                .POST(HttpRequest.BodyPublishers.ofString(parameters))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        token = response.body();
        //System.out.println(token);
        JsonObject jsonObject = JsonParser.parseString(token).getAsJsonObject();
        //JsonElement jsonElement = JsonParser.parseString(token);
        //JsonObject jsonObject = jsonElement.getAsJsonObject();
        accessToken = jsonObject.get("access_token").getAsString();

    }

    public static void categoriesRequest() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(spotifyResourceServer + "/v1/browse/categories"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String categories = response.body();
        //System.out.println(categories);

        JsonObject jsonObject = JsonParser.parseString(categories).getAsJsonObject();

        List<String> cat = new ArrayList<>();

        JsonObject categoriesObj = jsonObject.getAsJsonObject("categories");

        List<JsonObject> list = new ArrayList<>();
        for (JsonElement j : categoriesObj.getAsJsonArray("items")) {
            list.add(j.getAsJsonObject());
        }

        for (JsonObject jo : list) {
            viewer.add(jo.get("name").getAsString());
            categoryIDs.put(jo.get("name").getAsString(), jo.get("id").getAsString());
        }
        double x = viewer.size();
        totalPages = x / entriesPerPage;
        totalPages = StrictMath.ceil(totalPages);
        totalPages2 = (int)Math.round(totalPages);
    }

    public static void newReleasesRequest() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(spotifyResourceServer + "/v1/browse/new-releases"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String newReleases = response.body();

        JsonObject jsonObject = JsonParser.parseString(newReleases).getAsJsonObject();

        JsonObject newReleasesObj = jsonObject.getAsJsonObject("albums");

        List<JsonObject> itemsList = new ArrayList<>();
        List<ArrayList> list = new ArrayList<>();

        for (JsonElement j : newReleasesObj.getAsJsonArray("items")) {
            ArrayList<String> strings = new ArrayList<>();
            JsonObject jo = j.getAsJsonObject();
            itemsList.add(j.getAsJsonObject());
            JsonArray ja = jo.getAsJsonArray("artists");

            for (int i = 0; i < ja.size(); i ++) {
                String str = ja.get(i).getAsJsonObject().get("name").getAsString();
                strings.add(str);
            }
            list.add(strings);
        }

        List<JsonObject> externalURLs = new ArrayList<>();
        for (JsonObject j : itemsList) {
            externalURLs.add(j.getAsJsonObject("external_urls"));
        }

        LinkedList<String> albumLinks = new LinkedList<>();
        for (JsonObject jo2 : externalURLs) {
            String albumLink = jo2.get("spotify").getAsString();
            albumLinks.add(albumLink);

        }

        LinkedList<String> albumNames = new LinkedList<>();
        for (JsonObject jo : itemsList) {
            albumNames.add(jo.get("name").getAsString());
        }

        for (int i = 0; i < albumNames.size(); i ++) {
            String str = albumNames.get(i) +
                    "\n" +
                    list.get(i).toString() +
                    "\n" +
                    albumLinks.get(i) +
                    "\n";
            viewer.add(str);
        }
        double x = viewer.size();
        totalPages = x / entriesPerPage;
        totalPages = StrictMath.ceil(totalPages);
        totalPages2 = (int)Math.round(totalPages);

    }

    public static void featuredRequests() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(spotifyResourceServer + "/v1/browse/featured-playlists"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String featured = response.body();

        JsonObject jsonObject = JsonParser.parseString(featured).getAsJsonObject();

        JsonObject featuredObj = jsonObject.getAsJsonObject("playlists");

        List<JsonObject> itemsList = new ArrayList<>();
        for (JsonElement j : featuredObj.getAsJsonArray("items")) {
            itemsList.add(j.getAsJsonObject());
        }

        LinkedList<String> playlistNames = new LinkedList<>();

        for (JsonObject jo : itemsList) {
            playlistNames.add(jo.get("name").getAsString());
        }

        List<JsonObject> externalURLs = new ArrayList<>();
        for (JsonObject j : itemsList) {
            externalURLs.add(j.getAsJsonObject("external_urls"));
        }

        LinkedList<String> albumLinks = new LinkedList<>();
        for (JsonObject jo2 : externalURLs) {
            String albumLink = jo2.get("spotify").getAsString();
            albumLinks.add(albumLink);

        }

        for (int i = 0; i < playlistNames.size(); i++) {
            String str = playlistNames.get(i) +
                    "\n" +
                    albumLinks.get(i) +
                    "\n";
            viewer.add(str);
        }
        double x = viewer.size();
        totalPages = x / entriesPerPage;
        totalPages = StrictMath.ceil(totalPages);
        totalPages2 = (int)Math.round(totalPages);

    }

    public static void playlistRequests() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(spotifyResourceServer + "/v1/browse/categories/" + categoryIDs.get(categoryPlaylist) + "/playlists"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String playlist = response.body();
        try {
            JsonObject jsonObject = JsonParser.parseString(playlist).getAsJsonObject();

            JsonObject playlistObj = jsonObject.getAsJsonObject("playlists");

            List<JsonObject> itemsList = new ArrayList<>();
            for (JsonElement j : playlistObj.getAsJsonArray("items")) {
                itemsList.add(j.getAsJsonObject());
            }

            LinkedList<String> playlistNames = new LinkedList<>();

            for (JsonObject jo : itemsList) {
                playlistNames.add(jo.get("name").getAsString());
            }

            List<JsonObject> externalURLs = new ArrayList<>();
            for (JsonObject j : itemsList) {
                externalURLs.add(j.getAsJsonObject("external_urls"));
            }

            LinkedList<String> albumLinks = new LinkedList<>();
            for (JsonObject jo2 : externalURLs) {
                String albumLink = jo2.get("spotify").getAsString();
                albumLinks.add(albumLink);

            }

            for (int i = 0; i < playlistNames.size(); i++) {
                String str = playlistNames.get(i) +
                        "\n" +
                        albumLinks.get(i) +
                        "\n";
                viewer.add(str);
            }
            double x = viewer.size();
            totalPages = x / entriesPerPage;
            totalPages = StrictMath.ceil(totalPages);
            totalPages2 = (int)Math.round(totalPages);
        } catch (Exception e) {
            System.out.println(playlist);
        }
    }

}
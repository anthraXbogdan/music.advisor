import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

//Creates a local server
public class Server extends Main{

    public static void createServer() throws IOException, InterruptedException {

        server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);

    }

    public static void createHandler() {

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                query = exchange.getRequestURI().getQuery();

                if (query.equals("error=access_denied")) {
                    String codeNotFoundMessage = "Authorization code not found. Try again.";
                    exchange.sendResponseHeaders(200, codeNotFoundMessage.length());
                    exchange.getResponseBody().write(codeNotFoundMessage.getBytes());
                    exchange.getResponseBody().close();
                } else {
                    String codeFoundMessage = "Got the code. Return back to your program.";
                    exchange.sendResponseHeaders(200, codeFoundMessage.length());
                    exchange.getResponseBody().write(codeFoundMessage.getBytes());
                    exchange.getResponseBody().close();
                }
                accessCode = query.substring(5);
            }
        });

    }

    public static void createHandler2() {

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                query = exchange.getRequestURI().getQuery();
                String result;

                if (query != null && query.contains("code")) {
                    accessCode = query.substring(5);
                    result = "Got the code. Return back to your program.";
                } else {
                    result = "Not found authorization code. Try again.";
                }

                exchange.sendResponseHeaders(200,result.length());
                exchange.getResponseBody().write(result.getBytes());
                exchange.getResponseBody().close();
            }
        });

    }

}

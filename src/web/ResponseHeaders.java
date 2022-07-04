package web;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseHeaders {
    private static String size;
    private static String contentType;

    public ResponseHeaders(Path path) {
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            size = String.valueOf(Files.size(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ResponseHeaders(JSONObject jsonObject) {
        contentType = "Content-Type: application/json";
    }

    public static String createHeaders(Path path) {
        ResponseHeaders responseHeaders = new ResponseHeaders(path);
        return "Connection: Keep-Alive\nContent-Length: " + size + "\nContent-Type: " + contentType + "\n\n";
    }

    public static String createHeaders(JSONObject jsonObject) {
        ResponseHeaders responseHeaders = new ResponseHeaders(jsonObject);
        return "Connection: Keep-Alive\nContent-Length: " + size + "\nContent-Type: " + contentType + "\n\n";
    }

}

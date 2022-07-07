package web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseHeaders {
    private static String size;
    private static String contentType;

    private ResponseHeaders(Path path) {
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

    public static String createHeaders(Path path, String encodedContent) {
        ResponseHeaders responseHeaders = new ResponseHeaders(path);
        return "Connection: Keep-Alive\nContent-Length: " + size + "\nContent-Type: " + contentType + "\nAuthorization: Basic " + encodedContent +"\n\n";
    }
}

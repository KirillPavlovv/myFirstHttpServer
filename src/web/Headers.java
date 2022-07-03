package web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Headers {
    private static String size;
    private static String contentType;

    public Headers(Path path) {
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

    public static String createHeaders(Path path) {
        Headers headers = new Headers(path);
        return "Connection: Keep-Alive\nContent-Length: " + size + "\nContent-Type: " + contentType + "\n\n";
    }

}

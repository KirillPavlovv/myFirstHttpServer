package web;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Response {

    private static BufferedWriter output;

    public Response(Socket socket) throws IOException {
        output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private static final String CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8 = "Content-Type: text/html, charset=utf-8\n";
    private static final String HTTP_200_OK = "HTTP/1.1 200 OK\n";
    private static final String DEFAULT_PAGE = "main.html";

    static void fileResponse(Socket socket, Path path) throws IOException {
        OutputStream fileOutput = socket.getOutputStream();
        String encodedContent = "teretere";

        fileOutput.write((HTTP_200_OK).getBytes(StandardCharsets.UTF_8));
        fileOutput.write(ResponseHeaders.createHeaders(path, encodedContent).getBytes(StandardCharsets.UTF_8));
        fileOutput.write(Files.readAllBytes(path));
        fileOutput.flush();
        fileOutput.close();
    }

    static void showDefaultPage() throws IOException {
        writeOut(HTTP_200_OK);
        writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        writeOut("\n");
        transfer(Path.of(DEFAULT_PAGE));
        close();
    }

    static void badRequest() throws IOException {
        writeOut("HTTP/1.1 400 BAD_REQUEST\n");
        writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        writeOut("\n");
        writeOut("<h1> BAD REQUEST</h1>\n");
        writeOut("<h1> ERROR 400</h1>\n");
        close();
    }

    static Path urlNotFoundError() throws IOException {
        writeOut("HTTP/1.1 404 NOT_FOUND\n");
        writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        writeOut("\n");
        writeOut("<h1> URL NOT FOUND!</h1>\n");
        writeOut("<h1> ERROR 404</h1>\n");
        close();
        return null;
    }

    static void unauthorizedError() {

    }

    static void sendJsonResponse(JSONObject jsonObject) throws IOException {
        writeOut((HTTP_200_OK));
        writeOut("Content-Type: application/json\n");
        writeOut("\n");
        writeOut(jsonObject.toString());
        close();
    }

    public static void writeOut(String message) throws IOException {
            output.write(message);
    }

    public static void transfer(Path path) throws IOException {
            Files.newBufferedReader(path, StandardCharsets.UTF_8).transferTo(output);
    }

    public static void close() throws IOException {
        output.flush();
        output.close();
    }


}

package web;

import idnumber.IdService;
import org.json.JSONObject;
import salary.ResultResponse;
import salary.SalaryCalculation;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SocketService {

    private final Socket clientSocket;
    private BufferedReader input;

    public SocketService(Socket clientSocket) {
        this.clientSocket = clientSocket;
        System.out.println("Somebody is connected");
    }

    public void handleRequest() {
        try (clientSocket) {
            doHandleRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doHandleRequest() throws IOException {
        createInputStream();
        while (true) {
            if (ready()) break;
        }

        String firstLine = readLine();
        Request request = new Request(firstLine);
        System.out.println(firstLine);

        if (request.getMethod().equals("GET")) {
            while (ready()) {
                System.out.println(readLine());
            }
            handleGetRequest(request);
        }

        if (request.getMethod().equals("POST")) {
            String[] postContent = getPostContent();
            if (request.getPath().contains("addToList")) {
                JSONObject addToListContent = request.postRequestParametersToJson(postContent[1]);
                addToListPostRequest(addToListContent);
            }
            if (request.getPath().contains("addPicture")) {
                addPicturePostRequest(request, postContent);
            }
        }
    }

    private void handleGetRequest(Request request) throws IOException {
        if (request.getPath().contains("?")) {
            handleRequestParameters(request);
        } else if (request.getPath().equals("/")) {
            Response response = new Response(clientSocket);
            response.showDefaultPage();
        } else {
            Path path = urlNotFound(request.getPath());
            if (path == null) return;
            getAvailableFile(path);
        }
    }

    private void handleRequestParameters(Request request) throws IOException {
        request.setRequestParameters(clientSocket);
        if ((request.hasParameters())) {
            handleGetPersonalCodeGenerator(request);
            handleGetSalaryCalculator(request);
        }
    }

    private void getAvailableFile(Path path) throws IOException {
        Response.fileResponse(clientSocket, path);
    }

    private void handleGetSalaryCalculator(Request request) throws IOException {
        if (request.getPath().contains("salarycalculator")) {
            ResultResponse calculationResponse = SalaryCalculation.calculateSalary(request);
            Response response = new Response(clientSocket);
            response.sendJsonResponse(JsonHandler.salaryCalculationResponseToJson(calculationResponse));
        }
    }

    private void handleGetPersonalCodeGenerator(Request request) throws IOException {
        if (request.getPath().contains("idgenerator")) {
            String idNumber = IdService.generateId(request);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Personal code", idNumber);
            Response response = new Response(clientSocket);
            response.sendJsonResponse(jsonObject);
        }
    }

    private Path urlNotFound(String pathString) throws IOException {
        Path path = Paths.get(".", pathString);
        if (!Files.exists(path)) {
            Response response = new Response(clientSocket);
            return response.urlNotFoundError();
        }
        return path;
    }

    private void addPicturePostRequest(Request request, String[] postContent) {
        String uploadedFileName = getUploadedFileName(postContent);
        String filePath = ".pictures";
        byte[] image = postContent[2].getBytes(StandardCharsets.UTF_8);


    }

    private static String getUploadedFileName(String[] postContent) {
        String filename = "";
        String[] fileDataLines = postContent[1].split("\r\n");
        for (String fileDataLine : fileDataLines) {
            if (fileDataLine.contains("Content-Disposition")) {
                String[] lineParts = fileDataLine.split(" ");
                for (String linePart : lineParts) {
                    if (linePart.contains("filename")) {
                        String[] filenameContent = linePart.split("=");
                        return filenameContent[1].substring(1, filename.length() - 1);
                    }
                }
            }
        }
        return filename;
    }

    private String[] getPostContent() throws IOException {
        StringBuilder content = new StringBuilder();
        int c;
        while ((c = read()) != -1) {
            content.append((char) c);
        }
        return content.toString().split("\r\n\r\n");
    }


    private void addToListPostRequest(JSONObject jsonObject) throws IOException {
        writeToFile(jsonObject, "ListOfNames.txt");
    }

    private static void writeToFile(JSONObject jsonObject, String fileName) throws IOException {
        FileHandler fileHandler = new FileHandler(fileName);
        byte[] bytes = fileHandler.readFile();

        fileHandler.write(bytes);

        fileHandler.write(jsonObject);
        fileHandler.close();
    }

    public void createInputStream() throws IOException {
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String readLine() throws IOException {
        return input.readLine();
    }

    public boolean ready() throws IOException {
        return input.ready();
    }

    public int read() throws IOException {
        return input.read();
    }

}

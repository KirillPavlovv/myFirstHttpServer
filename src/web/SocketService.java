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
    private BufferedWriter output;
    private OutputStream fileOutput;

    public SocketService(Socket clientSocket) {
        this.clientSocket = clientSocket;
        System.out.println("Somebody is connected");
    }

    public void handleRequest() {
        try (clientSocket) {
            doHandleRequest();
            flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doHandleRequest() throws IOException {
        createStreams();
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
                handlePostRequest(request, postContent[1]);
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
            Response.showDefaultPage(this);
        } else {
            Path path = urlNotFound(request.getPath());
            if (path == null) return;
            getAvailableFile(path);
        }
    }

    private void handleRequestParameters(Request request) {
        request.setRequestParameters(this);
        if ((request.hasParameters())) {
            handleGetPersonalCodeGenerator(request);
            handleGetSalaryCalculator(request);
        }
    }

    private void getAvailableFile(Path path) throws IOException {
        createFileOutputStream();
        Response.fileResponse(this, path);
    }

    private void handleGetSalaryCalculator(Request request) {
        if (request.getPath().contains("salarycalculator")) {
            ResultResponse calculationResponse = SalaryCalculation.calculateSalary(request);
            Response.printSalaryCalculationResponse(this, calculationResponse);
        }
    }

    private void handleGetPersonalCodeGenerator(Request request) {
        if (request.getPath().contains("idgenerator")) {
            String idNumber = IdService.generateId(request);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Personal code", idNumber);
            Response.sendJsonResponse(this, jsonObject);
        }
    }

    private Path urlNotFound(String pathString) {
        Path path = Paths.get(".", pathString);
        if (!Files.exists(path)) {
            return Response.urlNotFoundError(this);
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

    private void handlePostRequest(Request request, String postContent) {
        if (request.getPath().contains("addToList")) {
            addToListPostRequest(request, postContent);
        }
    }

    private void addToListPostRequest(Request request, String postContent) {
        request.setPath(postContent);
        request.setRequestParameters(this);
        writeToFile(request, "ListOfNames.txt");
    }

    private static void writeToFile(Request request, String fileName) {
        String fullName = request.parameter1 + " " + request.parameter2 + "\n";
        FileHandler fileHandler = new FileHandler(fileName);
        byte[] bytes = fileHandler.readFile();

        fileHandler.write(bytes);

        fileHandler.write(fullName.getBytes(StandardCharsets.UTF_8));
        fileHandler.close();
    }

    public void createStreams() throws IOException {
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    public void createFileOutputStream() throws IOException {
        fileOutput = clientSocket.getOutputStream();
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

    public void transfer(Path path) {
        try {
            Files.newBufferedReader(path, StandardCharsets.UTF_8).transferTo(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String message) throws IOException {
        fileOutput.write((message).getBytes(StandardCharsets.UTF_8));
    }

    public void write(Path path) {
        try {
            fileOutput.write(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeOut(String message) {
        try {
            output.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flush() throws IOException {
        output.flush();
    }
}

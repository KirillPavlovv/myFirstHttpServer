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

public class Phone {

    private final Socket clientSocket;
    private BufferedReader input;
    private BufferedWriter output;
    private OutputStream fileOutput;

    public Phone(Socket clientSocket) {
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
        HttpRequest httpRequest = new HttpRequest(firstLine);
        System.out.println(firstLine);

        if (httpRequest.getMethod().equals("GET")) {
            while (ready()) {
                System.out.println(readLine());
            }
            handleGetRequest(httpRequest);
        }

        if (httpRequest.getMethod().equals("POST")) {
            String[] postContent = getPostContent();
            if (httpRequest.getPath().contains("addToList")) {
                handlePostRequest(httpRequest, postContent[1]);
            }
            if (httpRequest.getPath().contains("addPicture")) {
                addPicturePostRequest(httpRequest, postContent);
            }
        }
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

    private void addPicturePostRequest(HttpRequest httpRequest, String[] postContent) {
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

    private void handlePostRequest(HttpRequest httpRequest, String postContent) {
        if (httpRequest.getPath().contains("addToList")) {
            addToListPostRequest(httpRequest, postContent);
        }
    }

    private void addToListPostRequest(HttpRequest httpRequest, String postContent) {
        httpRequest.setPath(postContent);
        httpRequest.setRequestParameters(this);
        writeToFile(httpRequest, "ListOfNames.txt");
    }

    private static void writeToFile(HttpRequest httpRequest, String fileName) {
        String fullName = httpRequest.parameter1 + " " + httpRequest.parameter2 + "\n";
        FileHandler fileHandler = new FileHandler(fileName);
        byte[] bytes = fileHandler.readFile();

        fileHandler.write(bytes);

        fileHandler.write(fullName.getBytes(StandardCharsets.UTF_8));
        fileHandler.close();
    }

    private void handleGetRequest(HttpRequest httpRequest) throws IOException {
        if (httpRequest.getPath().contains("?")) {
            handleRequestParameters(httpRequest);
        } else if (httpRequest.getPath().equals("/")) {
            HttpResponseService.showDefaultPage(this);
        } else {
            Path path = urlNotFound(httpRequest.getPath());
            if (path == null) return;
            getAvailableFile(path);
        }
    }

    private void handleRequestParameters(HttpRequest httpRequest) {
        httpRequest.setRequestParameters(this);
        if ((httpRequest.hasParameters())) {
            handleGetPersonalCodeGenerator(httpRequest);
            handleGetSalaryCalculator(httpRequest);
        }
    }

    private void getAvailableFile(Path path) throws IOException {
        createFileOutputStream();
        HttpResponseService.fileResponse(this, path);
    }

    private void handleGetSalaryCalculator(HttpRequest httpRequest) {
        if (httpRequest.getPath().contains("salarycalculator")) {
            ResultResponse calculationResponse = SalaryCalculation.calculateSalary(httpRequest);
            HttpResponseService.printSalaryCalculationResponse(this, calculationResponse);
        }
    }

    private void handleGetPersonalCodeGenerator(HttpRequest httpRequest) {
        if (httpRequest.getPath().contains("idgenerator")) {
            String idNumber = IdService.generateId(httpRequest);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Personal code", idNumber);
            HttpResponseService.sendJsonResponse(this, jsonObject);
        }
    }

    private Path urlNotFound(String pathString) {
        Path path = Paths.get(".", pathString);
        if (!Files.exists(path)) {
            return HttpResponseService.urlNotFoundError(this);
        }
        return path;
    }
}

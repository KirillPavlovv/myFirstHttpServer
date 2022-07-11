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
import java.util.Base64;

import static web.Response.errorUnauthorized;

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
            JSONObject headers = getJsonHeaders();
            handleGetRequest(request, headers);
        }
        if (request.getMethod().equals("POST")) {
            String[] postContent = getPostContent();
            if (request.getPath().contains("addToList")) {
                addNameToList(request, postContent);
            }
            if (request.getPath().contains("uploadFile")) {
                uploadFilePostRequest(postContent);
            }
        }
    }

    private JSONObject getJsonHeaders() throws IOException {
        JSONObject headersJson = new JSONObject();
        while (ready()) {
            String line = readLine();
            if(!line.isEmpty()) {
                String[] lineParts = line.split(": ");
                headersJson.put(lineParts[0], lineParts[1]);
                System.out.println(line);
            }
        }
        return headersJson;
    }

    private void handleGetRequest(Request request, JSONObject headers) throws IOException {
        if (request.getPath().contains("?")) {
            handleRequestParameters(request);
        } else if (request.getPath().equals("/")) {
            Response response = new Response(clientSocket);
            response.showDefaultPage();
        } else {
            Path path = urlNotFound(request.getPath());
            if (path == null) return;
            if (request.getPath().contains("addtolist")) {
                if (headers.has("Authorization")) {
                    checkForAuthorization(headers, path);
                } else {
                    errorUnauthorized(clientSocket);
                }
            } else {
                String authorizationContent = "teretere";
                Response.fileResponse(clientSocket, path, authorizationContent);
            }

        }
    }

    private void checkForAuthorization(JSONObject headers, Path path) throws IOException {
        if (headers.has("Authorization")) {
            String authorizationContent = headers.get("Authorization").toString();
            String[] loginAndPasswordInArray = getNameAndPassFromHeaderAuthorizataion(authorizationContent);
            validation(loginAndPasswordInArray, authorizationContent, path);

        } else {
            errorUnauthorized(clientSocket);
        }
    }

    private String[] getNameAndPassFromHeaderAuthorizataion(String authorizationContent) {
        String[] contentParts = authorizationContent.split(" ");
        byte[] decode = Base64.getDecoder().decode(contentParts[1]);
        String loginAndPassword = new String(decode, StandardCharsets.UTF_8);
        return loginAndPassword.split(":");
    }

    private void validation(String[] loginAndPasswordInArray, String authorizationContent, Path path) throws IOException {
        String filename = "loginandpass.txt";
        String[] fileInfo = getInfoFromFile(filename);
        for (String s : fileInfo) {
            if (s.contains(loginAndPasswordInArray[0])) {
                handleBasicAuth(loginAndPasswordInArray, authorizationContent, path, s);
            }
        }
    }

    private void handleBasicAuth(String[] loginAndPasswordInArray, String authorizationContent, Path path, String s) throws IOException {
        String[] separateNameAndPass = s.split(":");
        String name = separateNameAndPass[0];
        String pass = separateNameAndPass[1];
        if (name.equals(loginAndPasswordInArray[0])) {
            if (pass.equals(loginAndPasswordInArray[1])) {
                Response.fileResponse(clientSocket, path, authorizationContent);
            } else {
                errorUnauthorized(clientSocket);
            }
        }
    }

    private String[] getInfoFromFile(String filename) throws IOException {
        FileInputStream input = new FileInputStream(filename);
        byte[] bytes = input.readAllBytes();
        String namesAndPasswords = new String(bytes, StandardCharsets.UTF_8);
        return namesAndPasswords.split("\r\n");
    }


    private void handleRequestParameters(Request request) throws IOException {
        request.setRequestParameters(clientSocket);
        if ((request.hasParameters())) {
            handleGetPersonalCodeGenerator(request);
            handleGetSalaryCalculator(request);
        }
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

    private void addNameToList(Request request, String[] postContent) throws IOException {
        String filename = "ListOfNames.txt";
        JSONObject addToListContent = request.postRequestParametersToJson(postContent[1]);
        writeToFile(addToListContent, filename);
    }

    private void uploadFilePostRequest(String[] postContent) throws IOException {
        String uploadedFileName = getUploadedFileName(postContent);
        byte[] imageBytes = postContent[2].getBytes(StandardCharsets.UTF_8);
        FileOutputStream out = new FileOutputStream(uploadedFileName);
        out.write(imageBytes);
        out.flush();
        out.close();

    }

    private String[] getPostContent() throws IOException {
        StringBuilder content = new StringBuilder();
        int c;
        while ((c = read()) != -1) {
            content.append((char) c);
        }
        return content.toString().split("\r\n\r\n");
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
                        return filenameContent[1].substring(1, (filenameContent[1].length() - 1));
                    }
                }
            }
        }
        return filename;
    }

    private static void writeToFile(JSONObject jsonObject, String fileName) throws IOException {
        FileInputStream input = new FileInputStream(fileName);
        byte[] bytes = input.readAllBytes();
        FileOutputStream output = new FileOutputStream(fileName);
        output.write(bytes);
        output.write((jsonObject.toString()+"\n").getBytes(StandardCharsets.UTF_8));
        input.close();
        output.close();
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

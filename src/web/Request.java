package web;

import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Request {
    private final String method;
    private final String path;
    private String parameter1;
    private String parameter2;

    public Request(String url) {
        String[] urlParts = url.split(" ");
        method = urlParts[0];
        path = urlParts[1];
    }

    public void setRequestParameters(Socket socket) throws IOException {
        String[] firstUrlPathSplit = path.split("&");
        List<String> stringList = new ArrayList<>();
        for (String s : firstUrlPathSplit) {
            String[] secondUrlPathSplit = s.split("=");
            if (secondUrlPathSplit.length == 1) {
                Response response = new Response(socket);
                response.badRequest();
            } else {
                stringList.add(secondUrlPathSplit[1]);
            }
        }
        if (stringList.size() == firstUrlPathSplit.length) {
            parameter1 = stringList.get(0);
            parameter2 = stringList.get(1);
        }
    }

    public JSONObject postRequestParametersToJson(String postContent) {
        JSONObject json = new JSONObject();
        String[] parameters = postContent.split("&");
        for (String parameter : parameters) {
            String[] keyValue = parameter.split("=");
            json.put(keyValue[0], keyValue[1]);
        }
        return json;
    }

    public boolean hasParameters() {
        if (parameter2 == null) {
            return false;
        } else return parameter1 != null;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getParameter1() {
        return parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

}

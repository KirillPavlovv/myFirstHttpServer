package web;

import java.util.ArrayList;
import java.util.List;

public class HttpRequest {
    String method;
    String path;
    String parameter1;
    String parameter2;

    public HttpRequest (String url) {
        String[] urlParts = url.split(" ");
        method = urlParts[0];
        path = urlParts[1];
        if (path.contains("?")) {
            String[] firstSplit = urlParts[1].split("&");
            List<String> stringList = new ArrayList<>();
            for (String s : firstSplit) {
                String[] split1 = s.split("=");
                stringList.add(split1[1]);
            }
            parameter1 = stringList.get(0);
            parameter2 = stringList.get(1);
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }
}

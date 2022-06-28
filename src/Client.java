import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8080);

        OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer.write("I need some info");
        writer.flush();
        System.out.println(reader.readLine());
        socket.close();
        writer.close();
        reader.close();


    }
}

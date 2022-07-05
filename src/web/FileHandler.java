package web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler{

    static FileOutputStream writer;
    static FileInputStream reader;

    public FileHandler(String fileName) {
        try {
            writer = new FileOutputStream(fileName);
            reader = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public byte[] readFile() {

        try {
            return reader.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(byte[] bytes) {
        try {
            writer.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            reader = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    public byte [] readFile() {

        byte[] bytes = new byte[0];
        try {
            bytes = reader.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public void write(byte [] bytes) {
        try {
            writer.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

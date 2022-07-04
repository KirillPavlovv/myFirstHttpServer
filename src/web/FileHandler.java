package web;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public byte[] readFile(String filename) {
        Path path = Paths.get(".", filename);
        try {
           return  Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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

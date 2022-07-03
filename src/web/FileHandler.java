package web;

import java.io.*;

public class FileHandler extends Writer {

    static FileOutputStream writer;
    static FileInputStream reader;

    public void createStreams(String fileName) {
        try {
            writer = new FileOutputStream(fileName);
            reader = new FileInputStream(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte [] readFile(String fileName) {
        try {
           return  reader.readAllBytes();
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

    public static void readAll(String fileName) {
        try {
            reader.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    @Override
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

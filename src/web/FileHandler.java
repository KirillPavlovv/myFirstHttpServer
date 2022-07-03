package web;

import java.io.*;

public class FileHandler extends Writer {

    static FileOutputStream writer;
    static FileInputStream reader;

    public static void createStreams(String fileName) {
        try {
            writer = new FileOutputStream(fileName);
            reader = new FileInputStream(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte [] readFile() {
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

    @Override
    public void write(char[] cbuf, int off, int len) {

    }

    @Override
    public void flush() {

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

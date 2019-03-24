package main;

import main.fs.FileSystem;

import java.util.Arrays;

public class Main {


    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        fs.create("Hello");
    }
}

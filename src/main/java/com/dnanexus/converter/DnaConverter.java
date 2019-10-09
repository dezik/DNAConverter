package com.dnanexus.converter;

import java.io.File;
import java.io.FileInputStream;

public class DnaConverter {
    public static void main(String[] args) throws Exception {
        File file = new File("./src/main/resources/data/input");
        byte[] fileData = new byte[(int) file.length()];
        FileInputStream in = new FileInputStream(file);
        in.read(fileData);
        in.close();

        for (byte i : fileData) {
            System.out.println(i + " = " +
                    String.format("%8s", Integer.toBinaryString(i & 0xFF)).replace(' ', '0')
            );
        }
    }
}

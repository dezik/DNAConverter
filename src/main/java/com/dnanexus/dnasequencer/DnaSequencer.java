package com.dnanexus.dnasequencer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class DnaSequencer {

    public static void main(String[] args) throws FileNotFoundException {
        System.out.print("Please enter the path to the file: ");
        Scanner in = new Scanner(System.in);
        String pathToFile = in.nextLine();
        System.out.print("Please enter L: ");
        int L = in.nextInt();
        String dnaSequence = sequenceDna(pathToFile, L);
        System.out.println("============OUTPUT============");
        System.out.println(dnaSequence);
    }

    public static String sequenceDna(String pathToFile, final int L) throws FileNotFoundException {
        if (L < 1) {
            throw new IllegalArgumentException("L have to be greater than 0.");
        }
        if (pathToFile == null || pathToFile.isEmpty()) {
            throw new IllegalArgumentException("Path to file should not be empty.");
        }

        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(new File(pathToFile)));
        StringBuilder dnaSequenceSb = new StringBuilder(L);
        StringBuilder qualityScoresSb = new StringBuilder(L);
        StringBuilder outputSb = new StringBuilder();
        int pieceIndex = 0;
        int position = 0;

        try {
            while (dataInputStream.available() > 0) {
                byte aByte = dataInputStream.readByte();
                String binaryString = convertByteToBinaryString(aByte);
                dnaSequenceSb.append(getDnaLetter(binaryString));
                qualityScoresSb.append(getQualityScore(binaryString));
                if (++position == L || dataInputStream.available() == 0) {
                    outputSb.append(getDnaPiece(++pieceIndex, dnaSequenceSb, qualityScoresSb));
                    position = 0;
                    dnaSequenceSb.setLength(0);
                    qualityScoresSb.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read data from the file...", e);
        }

        return outputSb.toString();
    }

    private static String getDnaPiece(int pieceIndex, StringBuilder dnaSequence, StringBuilder qualityScore) {
        return new StringBuilder().append("@READ_").append(pieceIndex).append(System.lineSeparator())
                .append(dnaSequence).append(System.lineSeparator())
                .append("+READ_").append(pieceIndex).append(System.lineSeparator())
                .append(qualityScore).append(System.lineSeparator()).toString();
    }

    private static String convertByteToBinaryString(byte aByte) {
        return String.format("%8s", Integer.toBinaryString(aByte & 0xFF)).replace(' ', '0');
    }

    private static char getDnaLetter(String binaryString) {
        switch (binaryString.substring(0, 2)) {
            case "00":
                return 'A';
            case "01":
                return 'C';
            case "10":
                return 'G';
            case "11":
                return 'T';
            default:
                throw new IllegalArgumentException("Provided binary string has wrong format.");
        }
    }

    private static char getQualityScore(String binaryString) {
        return (char) (Integer.parseInt(binaryString.substring(2), 2) + 33);
    }
}
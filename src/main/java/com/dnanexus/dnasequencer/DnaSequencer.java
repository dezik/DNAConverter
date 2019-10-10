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
        //check arguments validity
        if (L < 1) {
            throw new IllegalArgumentException("L have to be greater than 0.");
        }
        if (pathToFile == null || pathToFile.isEmpty()) {
            throw new IllegalArgumentException("Path to file should not be empty.");
        }

        //read file as data stream, throw FileNotFound if file is not there
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(new File(pathToFile)));

        //use string builders instead of strings for better concatenation performance
        StringBuilder dnaSequenceSb = new StringBuilder(L);
        StringBuilder qualityScoresSb = new StringBuilder(L);
        StringBuilder outputSb = new StringBuilder();

        int pieceIndex = 0;
        int position = 0;

        try {
            //loop while data stream has values
            while (dataInputStream.available() > 0) {
                //get actual available byte from data stream
                byte aByte = dataInputStream.readByte();

                //convert this byte to binary string representation
                String binaryString = convertByteToBinaryString(aByte);

                //calculate the DNA letter and append it to string builder
                dnaSequenceSb.append(getDnaLetter(binaryString));

                //calculate quality score and append it to string builder
                qualityScoresSb.append(getQualityScore(binaryString));

                //if we reached expected length or data stream is empty
                if (++position == L || dataInputStream.available() == 0) {
                    //generate DNA piece in FASTQ format and append it to output string builder
                    outputSb.append(getDnaPiece(++pieceIndex, dnaSequenceSb, qualityScoresSb));

                    //reset position and string builders
                    position = 0;
                    dnaSequenceSb.setLength(0);
                    qualityScoresSb.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error happen while read data from the file " + pathToFile, e);
        }

        return outputSb.toString();
    }

    //generate DNA piece in FASTQ format
    private static String getDnaPiece(int pieceIndex, StringBuilder dnaSequence, StringBuilder qualityScore) {
        String firstLineSequenceId = "@READ_" + pieceIndex;
        String thirdLineSequenceId = "+READ_" + pieceIndex;
        return new StringBuilder().append(firstLineSequenceId).append(System.lineSeparator())
                .append(dnaSequence).append(System.lineSeparator())
                .append(thirdLineSequenceId).append(System.lineSeparator())
                .append(qualityScore).append(System.lineSeparator()).toString();
    }

    //convert unsigned byte to binary string
    private static String convertByteToBinaryString(byte aByte) {
        return String.format("%8s", Integer.toBinaryString(aByte & 0xFF)).replace(' ', '0');
    }

    //get first 2 bits from the byte and translate them to DNA letter
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
                throw new IllegalArgumentException("Cannot parse DNA letter from binary string: " + binaryString);
        }
    }

    //get the last 6 bits from the byte, parse it to int and add 33, then cast to char
    private static char getQualityScore(String binaryString) {
        return (char) (Integer.parseInt(binaryString.substring(2), 2) + 33);
    }
}
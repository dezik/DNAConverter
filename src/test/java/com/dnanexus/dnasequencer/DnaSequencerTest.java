package com.dnanexus.dnasequencer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DnaSequencerTest {

    private static final String PATH_TO_VALID_INPUT_FILE = "./src/test/resources/data/input";
    private static final String PATH_TO_EMPTY_INPUT_FILE = "./src/test/resources/data/empty_file";
    private static final String PATH_TO_OUTPUT_FILE = "./src/test/resources/data/output";

    @ParameterizedTest
    @ValueSource(ints = {7, 15, 80})
    void shouldSequenceDnaCorrectly(int L) throws IOException {
        String actualResult = normalizeString(DnaSequencer.sequenceDna(PATH_TO_VALID_INPUT_FILE, L));
        String expectedResult = normalizeString(readFileAsString(PATH_TO_OUTPUT_FILE + L));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void shouldReturnEmptySequenceForEmptyFile() throws FileNotFoundException {
        assertEquals("", DnaSequencer.sequenceDna(PATH_TO_EMPTY_INPUT_FILE, 10));
    }

    @Test
    void shouldThrowExceptionIfPathToFileIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> DnaSequencer.sequenceDna("", 10));
    }

    @Test
    void shouldThrowExceptionIfCannotReadFile() {
        assertThrows(FileNotFoundException.class, () ->
                DnaSequencer.sequenceDna("./aaa/bbb/ccc/data/ddd", 1));
    }

    @Test
    void shouldThrowExceptionIfLLessThan1() {
        assertThrows(IllegalArgumentException.class, ()
                -> DnaSequencer.sequenceDna(PATH_TO_VALID_INPUT_FILE, 0));
    }

    static String readFileAsString(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    static String normalizeString(String string) {
        return string.replace("\r\n", System.lineSeparator())
                .replace("\r", System.lineSeparator())
                .trim();
    }
}
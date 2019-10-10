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

    @ParameterizedTest
    @ValueSource(ints = {7, 15, 80})
    void shouldSequenceDnaCorrectly(int L) throws IOException {
        String actualResult = normalize(DnaSequencer.sequenceDna("./src/test/resources/data/input", L));
        String expectedResult = normalize(readFileAsString("./src/test/resources/data/output" + L));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void shouldReturnEmptySequenceForEmptyFile() throws FileNotFoundException {
        assertEquals("", DnaSequencer.sequenceDna("./src/test/resources/data/empty_file", 10));
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
                -> DnaSequencer.sequenceDna("./src/test/resources/data/input", 0));
    }

    static String readFileAsString(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    static String normalize(String string) {
        return string.replace("\r\n", "\n").replace('\r', '\n').trim();
    }
}
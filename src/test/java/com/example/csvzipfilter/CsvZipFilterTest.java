package com.example.csvzipfilter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static org.junit.jupiter.api.Assertions.*;

public class CsvZipFilterTest {
    @TempDir Path tmp;

    @Test
    void endToEnd_defaults_allColumns() throws IOException {
        Path zip = makeZip(tmp.resolve("sample.zip"),
                new String[]{"a,b,c\n1,2,3\nx,y,z\n"},
                new String[]{"only,one\nrow,here\n"});
        CsvZipFilter p = new CsvZipFilter();
        Path outDir = p.process(zip, 1, Optional.empty(), ',', ';');
        assertTrue(Files.exists(outDir.resolve("filter_1_all_file0.csv")));
    }

    private Path makeZip(Path zipPath, String[]... csvContents) throws IOException {
        Files.createDirectories(zipPath.getParent());
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for (int i = 0; i < csvContents.length; i++) {
                zos.putNextEntry(new ZipEntry("file" + i + ".csv"));
                for (String piece : csvContents[i]) zos.write(piece.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
        }
        return zipPath;
    }
}

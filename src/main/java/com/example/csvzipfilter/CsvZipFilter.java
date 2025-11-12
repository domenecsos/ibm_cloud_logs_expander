package com.example.csvzipfilter;

import org.apache.commons.csv.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

public class CsvZipFilter {
    public Path process(Path zipFile, int firstColumn1, Optional<Integer> numColumns,
                        char inputSep, char outputSep) throws IOException {

        if (!Files.exists(zipFile)) throw new FileNotFoundException("ZIP not found: " + zipFile);
        if (firstColumn1 < 1) throw new IllegalArgumentException("firstColumn must be >= 1");

        Path outputDir = ZipUtils.unzipToSiblingFolder(zipFile);
        try (var stream = Files.walk(outputDir)) {
            stream.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase().endsWith(".csv"))
                  .forEach(csv -> {
                      try {
                          filterCsv(csv, firstColumn1, numColumns, inputSep, outputSep);
                      } catch (IOException e) {
                          throw new UncheckedIOException(e);
                      }
                  });
        } catch (UncheckedIOException uio) {
            throw uio.getCause();
        }
        return outputDir;
    }

    private void filterCsv(Path csvPath, int firstColumn1, Optional<Integer> numColumns,
                           char inputSep, char outputSep) throws IOException {

        String baseName = csvPath.getFileName().toString();
        String numLabel = numColumns.map(Object::toString).orElse("all");
        Path outPath = csvPath.getParent().resolve("filter_" + firstColumn1 + "_" + numLabel + "_" + baseName);

        CSVFormat inFormat = CSVFormat.DEFAULT.builder().setDelimiter(inputSep).setQuote('"').setRecordSeparator("\n").build();
        CSVFormat outFormat = CSVFormat.DEFAULT.builder().setDelimiter(outputSep).setQuote('"').setRecordSeparator("\n").build();

        try (Reader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, inFormat);
             Writer writer = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, outFormat)) {

            int startIdx0 = firstColumn1 - 1;
            for (CSVRecord record : parser) {
                int available = record.size();
                int count = numColumns.orElse(available - startIdx0);
                int endExclusive = Math.min(startIdx0 + count, available);
                if (startIdx0 >= available) {
                    printer.println();
                } else {
                    List<String> slice = record.toList().subList(startIdx0, endExclusive);
                    printer.printRecord(slice);
                }
            }
        }
    }
}

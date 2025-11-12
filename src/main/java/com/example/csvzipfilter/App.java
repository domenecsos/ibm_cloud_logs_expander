package com.example.csvzipfilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class App {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 5) {
            printUsageAndExit();
        }

        try {
            Path zipPath = Paths.get(args[0]);
            int firstColumn = 1;
            Optional<Integer> numColumns = Optional.empty();
            char inputSep = ',';
            char outputSep = ';';

            if (args.length >= 2 && !args[1].isBlank()) {
                firstColumn = Integer.parseInt(args[1]);
                if (firstColumn < 1) throw new IllegalArgumentException("firstColumn must be >= 1");
            }
            if (args.length >= 3 && !args[2].isBlank()) {
                if ("ALL".equalsIgnoreCase(args[2])) numColumns = Optional.empty();
                else {
                    int n = Integer.parseInt(args[2]);
                    if (n < 1) throw new IllegalArgumentException("numColumns must be >= 1 or 'ALL'");
                    numColumns = Optional.of(n);
                }
            }
            if (args.length >= 4 && !args[3].isBlank()) {
                if (args[3].length() != 1) throw new IllegalArgumentException("csvSeparator must be single char");
                inputSep = args[3].charAt(0);
            }
            if (args.length == 5 && !args[4].isBlank()) {
                if (args[4].length() != 1) throw new IllegalArgumentException("desiredSeparator must be single char");
                outputSep = args[4].charAt(0);
            }

            CsvZipFilter processor = new CsvZipFilter();
            Path outputDir = processor.process(zipPath, firstColumn, numColumns, inputSep, outputSep);
            System.out.println("Done. Files extracted to: " + outputDir.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            printUsageAndExit();
        }
    }

    private static void printUsageAndExit() {
        System.err.println("Usage:");
        System.err.println("  java -jar csv-zip-filter.jar <zipFile> [firstColumn] [numColumns] [csvSeparator] [desiredSeparator]");
        System.err.println("Defaults: firstColumn=1, numColumns=ALL, csvSeparator=',', desiredSeparator=';'");
        System.exit(1);
    }
}

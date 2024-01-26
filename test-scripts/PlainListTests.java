import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class PlainListTests {

    private static boolean fail = false;

    public static void main(String[] args) throws IOException {
        Files.walkFileTree(Path.of("./plain"), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String[] lines = Files.readAllLines(file).toArray(new String[0]);
                testForDuplicates(file.toString(), lines);
                testForWhiteSpace(file.toString(), lines);
                testSorted(file.toString(), lines);
                return FileVisitResult.CONTINUE;
            }
        });
        if (fail)
            System.exit(1);
    }

    private static void testForDuplicates(String file, String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            for (int j = 0; j < lines.length; j++) {
                if (lines[i].equals(lines[j]) && i != j) {
                    fail = true;
                    System.err.printf("%s:%s and %s duplicate word: \"%s\"%n", file, i, j, lines[i]);
                }
            }
        }
    }

    private static void testForWhiteSpace(String file, String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isBlank()) {
                fail = true;
                System.err.printf("%s:%s empty line%n", file, i + 1);
            } else if (lines[i].contains(" ") || lines[i].contains("\t")) {
                fail = true;
                System.err.printf("%s:%s line with whitespace%n", file, i + 1);
            }
        }
    }

    private static void testSorted(String file, String[] lines) {
        if (lines.length == 1)
            return;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].compareTo(lines[i - 1]) < 0) {
                fail = true;
                System.err.printf("%s:%s, %s wrong order: \"%s\", \"%s\"%n", file, (i - 1), i, lines[i - 1], lines[i]);
            }
        }
    }

}
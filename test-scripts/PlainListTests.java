import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PlainListTests {

    private static boolean fail = false;

    public static void main(String[] args) throws IOException {
        Files.walkFileTree(Path.of("./plain"), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                List<String> lines = Files.readAllLines(file).stream().toList();

                // test if sorted
                final List<String> sortedLines = lines.stream().sorted().toList();
                if (!lines.equals(sortedLines)) {
                    print("%s is not sorted%n", file);
                }
                lines = new ArrayList<>(sortedLines); // mutable list

                final Iterator<String> it = lines.listIterator();
                String previousLine = "";
                while (it.hasNext()) {
                    final String line = it.next();
                    if (line.isBlank()) {
                        fail = true;
                        it.remove();
                        print("%s empty line%n", file);
                    } else if (line.contains(" ") || line.contains("\t")) {
                        fail = true;
                        it.remove();
                        print("%s line with whitespace%n", file);
                    } else if (previousLine.equals(line)) { // remove duplicates
                        fail = true;
                        it.remove();
                        print("%s duplicate word: %s%n", file, line);
                    }
                    previousLine = line;
                }

                Optional<String> fileString = lines.stream().reduce((a, b) -> a + "\n" + b);
                if (fileString.isPresent())
                    Files.writeString(file, fileString.get());
                else {
                    fail = true;
                    print("could not create string to write to file");
                }

                return FileVisitResult.CONTINUE;
            }
        });
        if (fail)
            System.exit(1);
    }

    private static void print(String format, Object... objects) {
        System.err.printf(format, objects);
    }

}
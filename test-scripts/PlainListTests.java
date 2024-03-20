import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

boolean fail = false;

void main() throws IOException {
    Files.walkFileTree(Path.of("./plain"), new Visitor());
    if (fail)
        System.exit(1);
}

void print(String format, Object... objects) {
    System.err.printf(format, objects);
}

class Visitor extends SimpleFileVisitor<Path> {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        List<String> lines = Files.readAllLines(file).stream().toList();

        // test if sorted
        final List<String> sortedLines = lines.stream().sorted().toList();
        if (!lines.equals(sortedLines)) {
            fail = true;
            print("%s is not sorted%n", file);
        }
        lines = sortedLines;

        final List<String> newLines = new ArrayList<>(sortedLines.size());
        String previousLine = "";
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.isBlank()) {
                fail = true;
                print("%s empty line%n", file);
            } else if (line.contains(" ") || line.contains("\t")) {
                fail = true;
                String l = line.trim();
                if (line.contains(" ") || line.contains("\t")) {
                    newLines.add(l);
                }
                print("%s:%s line with whitespace%n", file, i);
            } else if (previousLine.equals(line)) { // remove duplicates
                fail = true;
                print("%s duplicate word: %s%n", file, line);
            } else {
                newLines.add(line);
            }
            previousLine = line;
        }

        Optional<String> fileString = newLines.stream().sorted().reduce((a, b) -> a + "\n" + b);
        if (fileString.isPresent())
            Files.writeString(file, fileString.get());
        else {
            fail = true;
            print("could not create string to write to file");
        }

        return FileVisitResult.CONTINUE;
    }
}

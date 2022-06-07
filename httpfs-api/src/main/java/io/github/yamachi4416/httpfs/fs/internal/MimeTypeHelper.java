package io.github.yamachi4416.httpfs.fs.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

public class MimeTypeHelper {

    private static Map<String, String> mimeTypes = readMimeTypes();

    private MimeTypeHelper() {
    }

    public static String getMimeType(Path path) {
        return getMimeType(path.getFileName().toString());
    }

    public static String getMimeType(String fileName) {
        var dotIdx = fileName.lastIndexOf(".");
        if (dotIdx == -1) {
            return null;
        }
        var ext = fileName.substring(dotIdx + 1);
        return mimeTypes.get(ext.toLowerCase(Locale.ENGLISH));
    }

    private static Map<String, String> readMimeTypes() {
        var map = new HashMap<String, String>();
        var loader = MimeTypeHelper.class.getClassLoader();

        try (var mimeTypes = loader.getResourceAsStream("mime.type");
                var br = new BufferedReader(new InputStreamReader(mimeTypes))) {
            br.lines()
                    .filter(line -> !line.isBlank())
                    .filter(line -> !line.startsWith("#"))
                    .map(line -> line.split("\\t+"))
                    .filter(cols -> cols.length > 1)
                    .forEach(cols -> {
                        var mimeType = cols[0].trim();
                        Stream.of(cols[1].split("\\s+"))
                                .forEach(ext -> map.putIfAbsent(ext, mimeType));
                    });
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

        return map;
    }
}

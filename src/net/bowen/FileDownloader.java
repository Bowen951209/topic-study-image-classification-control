package net.bowen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FileDownloader {
    /**
     * @param urlString the url of the file that you want to download
     * @param target    the path you want to store the file(must include file name)
     */
    public static void download(String urlString, String target) {
        try {
            URL url = new URL(urlString);
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            try (FileOutputStream fileOutputStream = new FileOutputStream(target)) {
                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

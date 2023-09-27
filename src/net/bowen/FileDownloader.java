package net.bowen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FileDownloader {
    private FileOutputStream fileOutputStream;
    private boolean wantListenProgress;
    private boolean isFinishDownload;
    private int progressListenPeriod;
    private URL url;

    public FileDownloader listenProgress(int period) {
        wantListenProgress = true;
        progressListenPeriod = period;
        return this;
    }

    /**
     * @param urlString the url of the file that you want to download
     * @param target    the path you want to store the file(must include file name)
     */
    public void download(String urlString, String target) {
        try {
            this.url = new URL(urlString);
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            fileOutputStream = new FileOutputStream(target);
            if (wantListenProgress) printProgress();

            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            isFinishDownload = true;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printProgress() {
        Thread progressPrinter = new Thread(() -> {
            try {
                double wholeSize =
                        url.openConnection().getContentLength() * 0.00000095367432;
                long startTime = System.currentTimeMillis();
                long elapsedTime = 0;
                do {
                    if (elapsedTime <= progressListenPeriod) {
                        elapsedTime = System.currentTimeMillis() - startTime;
                        continue;
                    }

                    elapsedTime = 0;
                    startTime = System.currentTimeMillis();

                    double currentSizeMB = fileOutputStream.getChannel().size() * 0.00000095367432;
                    System.out.printf("%.2f MB / %.2f MB\n", currentSizeMB, wholeSize);
                } while (!isFinishDownload);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        progressPrinter.start();
    }
}

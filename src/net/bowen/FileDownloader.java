package net.bowen;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FileDownloader {
    private FileOutputStream fileOutputStream;
    private Timer progressTimer;
    private boolean wantListenProgress;
    private boolean isFinishDownload;
    private int progressListenPeriod;
    private double currentSizeMB, wholeSize, percentage;
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

            // Print the 100%
            System.out.printf("\r[■■■■■■■■■■■■■■■■■■■■] %.2f MB / %.2f MB\n", wholeSize, wholeSize);

            if (progressTimer != null) progressTimer.stop();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printProgress() {
        // TODO: 2023/11/8 Use StringBuilder
        progressTimer = new Timer(progressListenPeriod, (event -> {
            try {
                String barString;
                wholeSize = url.openConnection().getContentLength() * 0.00000095367432;
                currentSizeMB = fileOutputStream.getChannel().size() * 0.00000095367432;
                percentage = currentSizeMB / wholeSize * 100;
                barString = "[                    ]";// 20 blanks
                for (int i = 0; i < (int) (percentage / 5); i++)
                    barString = barString.replaceFirst(" ", "■");


                // If the "if-statement" is not here, progress printing will have bug.
                // The reason I think is that this timer is called even after the download is
                // complete. So I just make sure we only print when !isFinishDownload.
                if (!isFinishDownload)
                    System.out.printf("\r%s %.2f MB / %.2f MB", barString, currentSizeMB, wholeSize);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        progressTimer.start();
    }
}

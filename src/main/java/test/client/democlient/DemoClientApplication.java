package test.client.democlient;

import org.slf4j.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.io.*;
import java.net.*;
import java.util.List;

@SpringBootApplication
public class DemoClientApplication implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DemoClientApplication.class);
    private final RepoMyFile repoMyFile;

    private final Api api;

    public DemoClientApplication(RepoMyFile repoMyFile, Api api) {
        this.repoMyFile = repoMyFile;
        this.api = api;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoClientApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        List<MyFile> allByDownload = repoMyFile.getAllByDownload(false);
        if (!allByDownload.isEmpty()) {
            for (MyFile myFile : allByDownload) {
                URL url = myFile.getUrl();
                File file = myFile.getFile();
                long removeFileSize = myFile.getRemoveFileSize();
                HttpURLConnection httpConnection = null;
                try {
                    httpConnection = (HttpURLConnection)url.openConnection();
                } catch (IOException ioException) {
                    logger.error("IOException при создании нового соединения ", ioException);
                }
                httpConnection.setRequestProperty("Range", "bytes=" + file.length() + "-" + removeFileSize);

                api.download(httpConnection, myFile);
            }
        }
    }
}

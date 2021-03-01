package test.client.democlient;

import org.slf4j.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.*;
import java.nio.channels.*;

@RestController
public class Api {

    public static final long MILLIS = 10000L;

    public static String FILE_URL = "http://localhost:8080/fsr";
//    public static String FILE_URL = "http://localhost:8080/srbr";

//    public static String FILE_URL = "http://localhost:8080/srb";


    public static String FILE_NAME = "C:\\Work\\SDF\\ServerFiles\\client.iso";

    private static final Logger logger = LoggerFactory.getLogger(Api.class);

    private final RepoMyFile repoMyFile;

    public Api(RepoMyFile repoMyFile) {
        this.repoMyFile = repoMyFile;
    }

    @GetMapping("/start")
    public void start() {
        MyFile myFile = new MyFile();
        File file = new File(FILE_NAME);
        URL url = null;
        try {
            url = new URL(FILE_URL);
            boolean serverFlag = true;
            HttpURLConnection httpConnection;
            long removeFileSize;

            //жду когда сервер будет доступен
            do {
                httpConnection = (HttpURLConnection)url.openConnection();
                removeFileSize = httpConnection.getContentLengthLong();
                if (removeFileSize == -1L) {
                    serverFlag = false;
                    try {
                        logger.info("Сервер недоступен жду " + MILLIS / 1000 + "c");
                        Thread.sleep(MILLIS);
                    } catch (InterruptedException interruptedException) {
                        logger.error("Thread.sleep", interruptedException);
                    }
                } else {
                    serverFlag = true;
                    myFile.setFile(file);
                    myFile.setDownload(false);
                    myFile.setRemoveFileSize(removeFileSize);
                    myFile.setUrl(url);
                    repoMyFile.save(myFile);
                }
            }
            while (!serverFlag);
            download(httpConnection, myFile);
        } catch (MalformedURLException e) {
            logger.error("Ошибка в URL ", e);
        } catch (IOException e) {
            logger.error("IOException при создании нового соединения ", e);
        }
    }

    public void download(HttpURLConnection hC, MyFile myFile) {
        File file = myFile.getFile();
        long removeFileSize = myFile.getRemoveFileSize();
        URL url = myFile.getUrl();
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        ReadableByteChannel readableByteChannel = null;
        FileChannel channel = null;
        long rangeStart = 0L;

        String rangeHeader = hC.getRequestProperty("Range");
        try {
            if (rangeHeader != null) {
                String[] ranges = rangeHeader.split("-");
                rangeStart = Long.parseLong(ranges[0].substring(6));
                fileOutputStream = new FileOutputStream(file, true);
                logger.info("Докачиваю файл");
            } else {
                logger.info("Скачиваю файл");
                fileOutputStream = new FileOutputStream(file);
            }
            inputStream = hC.getInputStream();
            readableByteChannel = Channels.newChannel(inputStream);
            channel = fileOutputStream.getChannel();
            channel.transferFrom(readableByteChannel, rangeStart, removeFileSize);

            if (file.length() == removeFileSize) {
                myFile.setDownload(true);
                repoMyFile.save(myFile);
                logger.info("Файл скачан");
            } else {
                throw new DownloadException("Файл скачан не полностью");
            }
        } catch (FileNotFoundException e) {
            logger.error("Файл не найден", e);
        } catch (ConnectException | DownloadException e) {
            try {
                logger.info("Сервер недоступен жду " + MILLIS / 1000 + "c");
                Thread.sleep(MILLIS);
            } catch (InterruptedException interruptedException) {
                logger.error("Thread.sleep", interruptedException);
            }
            hC.disconnect();
            HttpURLConnection httpConnection = null;
            try {
                httpConnection = (HttpURLConnection)url.openConnection();
            } catch (IOException ioException) {
                logger.error("IOException при создании нового соединения ", ioException);
            }
            httpConnection.setRequestProperty("Range", "bytes=" + file.length() + "-" + removeFileSize);
//            logger.info("Докачиваю файл");
            download(httpConnection, myFile);
        } catch (IOException e) {
            logger.error("IOException в методе download", e);
        } finally {
            if (fileOutputStream != null) {
                hC.disconnect();
                try {
                    fileOutputStream.close();
                    if (inputStream!=null){
                        inputStream.close();
                    }
                    if (readableByteChannel != null){
                        readableByteChannel.close();
                    }
                    if (channel != null){
                        channel.close();
                    }
                } catch (IOException e) {
                    logger.error("Ошибка при закрытии потоков", e);
                }
            }
        }
    }

    //    public static void downloadAllFile(File file, URL url) {
//        FileOutputStream fileOutputStream = null;
//        InputStream inputStream = null;
//        HttpURLConnection httpConnection = null;
//        ReadableByteChannel readableByteChannel = null;
//        FileChannel channel = null;
//        long removeFileSize = 0;
//        try {
//            httpConnection = (HttpURLConnection)url.openConnection();
//            removeFileSize = httpConnection.getContentLengthLong();
//            inputStream = httpConnection.getInputStream();
//            readableByteChannel = Channels.newChannel(inputStream);
//            fileOutputStream = new FileOutputStream(file);
//            channel = fileOutputStream.getChannel();
//            long startPosition = 0L;
//            channel.transferFrom(readableByteChannel, startPosition, removeFileSize);
//            if (file.length() != removeFileSize) {
//                logger.info("Файл скачан не полностью");
//                throw new DownloadException("Файл скачан не полностью");
//            } else {
//                logger.info("Файл скачан");
//            }
//        } catch (DownloadException | ConnectException e) {
//            if (e instanceof ConnectException) {
//                logger.error("Сервер недоступен", e);
//            }
//            downloadRangeFile(file, url, file.length(), removeFileSize);
//        } catch (IOException e) {
//            logger.error("IOException в методе downloadAllFile", e);
//        } finally {
//            try {
//                channel.close();
//                fileOutputStream.close();
//                readableByteChannel.close();
//                inputStream.close();
//            } catch (IOException e) {
//                logger.error("Ошибка при закрытии потоков", e);
//            }
//            httpConnection.disconnect();
//        }
//    }
//
//    public static void downloadRangeFile(File file, URL url, long startPosition, long removeFileSize) {
//        FileOutputStream fileOutputStream = null;
//        InputStream inputStream = null;
//        HttpURLConnection httpConnection = null;
//        ReadableByteChannel readableByteChannel = null;
//        FileChannel channel = null;
//        do {
//            try {
//                httpConnection = (HttpURLConnection)url.openConnection();
//                httpConnection.setRequestProperty("Range", "bytes=" + startPosition + "-" + removeFileSize);
//                inputStream = httpConnection.getInputStream();
//                readableByteChannel = Channels.newChannel(inputStream);
//                fileOutputStream = new FileOutputStream(file, true);
//                channel = fileOutputStream.getChannel();
//                channel.transferFrom(readableByteChannel, startPosition, removeFileSize);
//            } catch (IOException e) {
//                logger.error("IOException в методе downloadRangeFile", e);
//            } finally {
//                try {
//                    channel.close();
//                    fileOutputStream.close();
//                    readableByteChannel.close();
//                    inputStream.close();
//                } catch (IOException e) {
//                    logger.error("Ошибка при закрытии потоков", e);
//                }
//                httpConnection.disconnect();
//            }
//        } while (file.length() != removeFileSize);
//    }
}

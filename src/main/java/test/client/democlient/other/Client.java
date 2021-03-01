package test.client.democlient.other;

import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.nio.channels.*;

public class Client {
    public static String FILE_URL = "http://localhost:8080/fsr";
    public static String FILE_NAME = "C:\\Work\\SDF\\ServerFiles\\client.iso";
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException {
        URL url = null;
        HttpURLConnection httpConnection = null;
        try {
            url = new URL(FILE_URL);
            System.out.println("sleep");
            Thread.sleep(10000);
            httpConnection = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            logger.error("Неверный url", e);
        } catch (IOException e) {
            logger.error("Ошибка подключения", e);
        } catch (InterruptedException e) {
            logger.error("Ошибка sleep", e);
        }
        long removeFileSize = httpConnection.getContentLengthLong();
        System.out.println(removeFileSize);

        ReadableByteChannel readableByteChannel = null;
        try {
            readableByteChannel = Channels.newChannel(httpConnection.getInputStream());
        } catch (IOException e) {
            logger.error("Ошибка getInputStream", e);
        }
        FileOutputStream fileOutputStream = null;
        File file = new File (FILE_NAME);
        try {

            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("Ошибка fileOutputStream", e);
        }
        FileChannel channel = fileOutputStream.getChannel();
        try {
            channel.transferFrom(readableByteChannel, 0L, removeFileSize);
        } catch (IOException e) {
            logger.error("Ошибка transferFrom", e);
        }
        System.out.println("end");
        System.out.println(file.length());
    }

//    public static void download(URL url, HttpURLConnection httpConnection, long removeFileSize){
//        ReadableByteChannel readableByteChannel = Channels.newChannel(httpConnection.getInputStream());
//        FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME, true);
//        FileChannel channel = fileOutputStream.getChannel();
//        channel.transferFrom(readableByteChannel, 0L, removeFileSize);
//        System.out.println("end");
//    }
}

//        httpConnection.setRequestProperty("Range", "bytes=0-5");
//        FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME, true);

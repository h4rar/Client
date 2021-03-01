package test.client.democlient.other;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class Test {

    private static String PATH = "C:\\Work\\SDF\\ServerFiles\\test.txt";

    public static void main(String[] args) throws IOException {
//        RandomAccessFile raf = new RandomAccessFile(PATH, "r");
//        raf.seek(1L);
//        byte[] bytes = new byte[5];
//        raf.read(bytes);
//        raf.close();
//        System.out.println(new String(bytes));

//        FileInputStream stream = new FileInputStream();
        givenFile_whenReadWithFileChannelUsingRandomAccessFile_thenCorrect();
    }

    public static void givenFile_whenReadWithFileChannelUsingRandomAccessFile_thenCorrect()
            throws IOException {
        RandomAccessFile reader = new RandomAccessFile(PATH, "r");
        reader.seek(1L);
        FileChannel channel = reader.getChannel();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bufferSize = 1024;
        if (bufferSize > channel.size()) {
            bufferSize = (int)channel.size();
        }
        ByteBuffer buff = ByteBuffer.allocate(bufferSize);
        while (channel.read(buff) > 0) {
            out.write(buff.array(), 0, buff.position());
            buff.clear();
        }
        String fileContent = new String(out.toByteArray());
        System.out.println(fileContent);
    }
}

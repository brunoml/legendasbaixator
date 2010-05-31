package Hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * Created by IntelliJ IDEA.
 * User: Brunol
 * Date: 20/05/2010
 * Time: 10:50:01
 * To change this template use File | Settings | File Templates.
 */
public class SubDBHasher {
    /**
     * Size of the chunks that will be hashed in bytes (64 KB)
     */
    private static final int HASH_CHUNK_SIZE = 64 * 1024;

    public static String computeHash(File file) {
        long size = file.length();
        long chunkSizeForFile = Math.min(HASH_CHUNK_SIZE, size);

        try {
            FileChannel fileChannel = new FileInputStream(file).getChannel();
            try {
                long head = computeHashForChunk(fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, chunkSizeForFile));
                long tail = computeHashForChunk(fileChannel.map(FileChannel.MapMode.READ_ONLY, Math.max(size - HASH_CHUNK_SIZE, 0), chunkSizeForFile));

                MessageDigest md5 = MessageDigest.getInstance("MD5");
                return stringHexa(md5.digest(String.valueOf(head + tail).getBytes()));
            } finally {
                fileChannel.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static String stringHexa(byte[] bytes) {
       StringBuilder s = new StringBuilder();
       for (int i = 0; i < bytes.length; i++) {
           int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
           int parteBaixa = bytes[i] & 0xf;
           if (parteAlta == 0) s.append('0');
           s.append(Integer.toHexString(parteAlta | parteBaixa));
       }
       return s.toString();
    }

    private static long computeHashForChunk(ByteBuffer buffer) {

        LongBuffer longBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
        long hash = 0;

        while (longBuffer.hasRemaining()) {
            hash += longBuffer.get();
        }

        return hash;
    }
}

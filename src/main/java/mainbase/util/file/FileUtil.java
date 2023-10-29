package mainbase.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtil {

    public static byte[] fileToByteArray(File file) throws IOException {
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("File is too large to fit in a byte array");
        }

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] byteArray = new byte[(int) fileSize];
        int bytesRead = 0;

        while (bytesRead < fileSize) {
            int bytesToRead = (int) fileSize - bytesRead;
            int bytesReadNow = fileInputStream.read(byteArray, bytesRead, bytesToRead);
            if (bytesReadNow == -1) {
                break;
            }
            bytesRead += bytesReadNow;
        }

        fileInputStream.close();

        if (bytesRead < fileSize) {
            throw new IOException("Could not read the entire file into the byte array");
        }

        return byteArray;
    }

}

package mainbase.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import mainbase.functional.JsonFileProcessMethod;

public class FileUtil {
    protected static Logger log = LoggerFactory.getLogger(FileUtil.class);

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

    public static void processJsonFile(String path, String filename, JsonFileProcessMethod method, Object... params)
            throws IOException, IllegalArgumentException {
        if (StringUtils.isBlank(path) || StringUtils.isBlank(filename) || method == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        File jsonFile = new File(path, filename);

        if (!jsonFile.exists() || jsonFile.isDirectory()) {
            throw new IllegalArgumentException("File does not exist or is a directory");
        }

        try (JsonParser jsonParser = new JsonFactory().createParser(jsonFile)) {
            method.process(jsonParser, params);
        } catch (IOException ex) {
            throw new IOException("Error processing JSON file", ex);
        }
    }

}

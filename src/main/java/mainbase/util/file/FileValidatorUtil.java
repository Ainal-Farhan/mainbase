package mainbase.util.file;

import java.io.File;
import java.io.IOException;

import org.apache.commons.compress.archivers.zip.ZipFile;

public class FileValidatorUtil {
	public static boolean isValidDocx(String filePath, String filename) {
		File file = new File(filePath, filename);
		if (file.exists()) {
			try (ZipFile zipFile = new ZipFile(file)) {
				// Check if the ZIP archive contains the required files/folders
				if (zipFile.getEntry("word/document.xml") != null && zipFile.getEntry("_rels/.rels") != null
						&& zipFile.getEntry("docProps/core.xml") != null) {
					return true;
				}

				return false;

			} catch (IOException e) {
			}
		}
		return false;
	}
}

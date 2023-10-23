package mainbase.util.path;

import java.io.File;

public class ProjectPathUtil {
	public static final String PROJECT_ROOT_DIR;
	public static final String PROJECT_OUTPUT_DIR;
	public static final String TEMPLATE_DIR;
	public static final String FILE_OUTPUT_DIR;
	public static final String TEMPLATE_OUTPUT_DIR;

	static {
		PROJECT_ROOT_DIR = System.getProperty("user.dir");
		PROJECT_OUTPUT_DIR = System.getenv("mainbase");

		File templateDirectory = new File(PROJECT_ROOT_DIR + "/src/main/reference/template");
		TEMPLATE_DIR = templateDirectory.exists() && templateDirectory.isDirectory()
				? templateDirectory.getAbsolutePath()
				: "";

		FILE_OUTPUT_DIR = PROJECT_OUTPUT_DIR + "/temp/files";
		TEMPLATE_OUTPUT_DIR = FILE_OUTPUT_DIR + "/template";
	}

}

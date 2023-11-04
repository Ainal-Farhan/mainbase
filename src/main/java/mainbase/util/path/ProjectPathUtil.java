package mainbase.util.path;

import java.io.File;

public class ProjectPathUtil {
    public static final String PROJECT_ROOT_DIR;
    public static final String PROJECT_CONFIG_DIR;
    public static final String PROJECT_OUTPUT_DIR;
    public static final String TEMPLATE_DIR;
    public static final String TEMPLATE_REF_DIR;
    public static final String RESOURCE_DIR;
    public static final String FILE_OUTPUT_DIR;
    public static final String TEMPLATE_OUTPUT_DIR;

    static {
        PROJECT_ROOT_DIR = System.getProperty("user.dir");
        PROJECT_OUTPUT_DIR = System.getenv("mainbase");

        File templateDirectory = new File(PROJECT_ROOT_DIR + "/src/main/resources/templates");
        TEMPLATE_DIR = templateDirectory.exists() && templateDirectory.isDirectory()
                ? templateDirectory.getAbsolutePath()
                : "";

        File templateRefDirectory = new File(TEMPLATE_DIR + "/reference");
        TEMPLATE_REF_DIR = templateRefDirectory.exists() && templateRefDirectory.isDirectory()
                ? templateRefDirectory.getAbsolutePath()
                : "";

        File configDirectory = new File(PROJECT_ROOT_DIR + "/src/main/resources/config");
        PROJECT_CONFIG_DIR = configDirectory.exists() && configDirectory.isDirectory()
                ? configDirectory.getAbsolutePath()
                : "";

        File resourceDirectory = new File(PROJECT_ROOT_DIR + "/src/main/resources/images");
        RESOURCE_DIR = resourceDirectory.exists() && resourceDirectory.isDirectory()
                ? resourceDirectory.getAbsolutePath()
                : "";

        File outputFileDirectory = new File(PROJECT_OUTPUT_DIR + "/temp/files");
        FILE_OUTPUT_DIR = (outputFileDirectory.exists() || outputFileDirectory.mkdirs())
                && outputFileDirectory.isDirectory() ? outputFileDirectory.getAbsolutePath() : "";

        File templateOutputFileDirectory = new File(FILE_OUTPUT_DIR + "/template");
        TEMPLATE_OUTPUT_DIR = outputFileDirectory.exists()
                && (templateOutputFileDirectory.exists() || templateOutputFileDirectory.mkdirs())
                && templateOutputFileDirectory.isDirectory() ? templateOutputFileDirectory.getAbsolutePath() : "";
    }

}

package mainbase.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonToken;

import mainbase.util.file.FileUtil;
import mainbase.util.path.ProjectPathUtil;

public class TemplateConfig extends Config {
    private static final long serialVersionUID = 1L;
    private static TemplateConfig config;
    private static String FILENAME = "template.config.json";
    private static String KEY_OBJECTARRAY_TEMPLATES = "templates";

    private static String KEY_STRING_FILENAME = "filename";
    private static String KEY_STRING_KEY = "key";

    private static String KEY_OBJECTARRAY_ACTIONS = "actions";
    private static String KEY_STRING_TYPE = "type";
    private static String KEY_STRINGARRAY_EXCLUDED_CONTENT_CONTROL_LIST = "excluded_content_control_list";

    private final Map<String, String> fileNameMap;
    private final Map<String, Map<String, List<String>>> excludedContentControlListMap;

    private TemplateConfig() {
        super();

        fileNameMap = new HashMap<>();
        excludedContentControlListMap = new HashMap<>();
    }

    public static TemplateConfig getInstance() {
        if (config == null) {
            config = new TemplateConfig();
            config.init();
        }

        return config;
    }

    @Override
    protected void init() {
        try {
            FileUtil.processJsonFile(ProjectPathUtil.PROJECT_CONFIG_DIR, FILENAME, (jsonParser, objects) -> {
                while (jsonParser.nextToken() != null) {
                    if (jsonParser.currentToken() == JsonToken.FIELD_NAME
                            && KEY_OBJECTARRAY_TEMPLATES.equals(jsonParser.getText())) {
                        jsonParser.nextToken();
                        if (jsonParser.currentToken() == JsonToken.START_ARRAY) {
                            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {

                                if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
                                    String key = null;
                                    String filename = null;
                                    Map<String, List<String>> excludedContentControlMap = new HashMap<>();

                                    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                        if (jsonParser.currentToken() == JsonToken.FIELD_NAME
                                                && KEY_STRING_KEY.equals(jsonParser.getText())) {
                                            jsonParser.nextToken();
                                            key = jsonParser.getValueAsString();
                                        } else if (jsonParser.currentToken() == JsonToken.FIELD_NAME
                                                && KEY_STRING_FILENAME.equals(jsonParser.getText())) {
                                            jsonParser.nextToken();
                                            filename = jsonParser.getValueAsString();
                                        } else if (jsonParser.currentToken() == JsonToken.FIELD_NAME
                                                && KEY_OBJECTARRAY_ACTIONS.equals(jsonParser.getText())) {
                                            jsonParser.nextToken();
                                            if (jsonParser.currentToken() == JsonToken.START_ARRAY) {
                                                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                                    if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
                                                        String type = null;
                                                        List<String> excludedContentControlList = new ArrayList<>();
                                                        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                                            if (jsonParser.currentToken() == JsonToken.FIELD_NAME
                                                                    && KEY_STRING_TYPE.equals(jsonParser.getText())) {
                                                                jsonParser.nextToken();
                                                                type = jsonParser.getValueAsString();
                                                            } else if (jsonParser.currentToken() == JsonToken.FIELD_NAME
                                                                    && KEY_STRINGARRAY_EXCLUDED_CONTENT_CONTROL_LIST
                                                                            .equals(jsonParser.getText())) {
                                                                jsonParser.nextToken();
                                                                if (jsonParser
                                                                        .currentToken() == JsonToken.START_ARRAY) {
                                                                    while (jsonParser
                                                                            .nextToken() != JsonToken.END_ARRAY) {
                                                                        excludedContentControlList
                                                                                .add(jsonParser.getValueAsString());
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (type != null) {
                                                            excludedContentControlMap.put(type,
                                                                    excludedContentControlList);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (key != null) {
                                        fileNameMap.put(key, filename);
                                        excludedContentControlListMap.put(key, excludedContentControlMap);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        } catch (IllegalArgumentException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public Map<String, String> getFileNameMap() {
        return fileNameMap;
    }

    public Map<String, Map<String, List<String>>> getExcludedContentControlListMap() {
        return excludedContentControlListMap;
    }
}

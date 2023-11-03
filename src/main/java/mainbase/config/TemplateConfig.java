package mainbase.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import mainbase.enums.TemplateContentControlLocationEnum;
import mainbase.util.docx4j.TemplateUtil;
import mainbase.util.file.FileUtil;
import mainbase.util.path.ProjectPathUtil;

public class TemplateConfig extends Config {
    private static final long serialVersionUID = 1L;
    private static TemplateConfig config;
    private static String CONFIG_FILE_NAME = "template.config.json";
    private static String KEY_OBJECTARRAY_TEMPLATES = "templates";

    private static String KEY_STRING_FILENAME = "filename";
    private static String KEY_STRING_KEY = "key";

    private static String KEY_OBJECTARRAY_ACTIONS = "actions";
    private static String KEY_STRING_TYPE = "type";
    private static String KEY_STRINGARRAY_EXCLUDED_CONTENT_CONTROL_LIST = "excluded_content_control_list";
    private static String KEY_STRINGARRAY_INCLUDED_CONTENT_CONTROL_LIST = "included_content_control_list";
    private static String KEY_BOOLEAN_FLAG_INSERT_ALL = "flag_insert_all";

    private final Map<String, TemplateProperty> templatePropertiesMap;
    private static Boolean firstInit = Boolean.TRUE;

    private class TemplateProperty {
        protected String key;
        protected String filename;
        protected final Map<String, TemplateAction> templateActionMap;
        protected Map<TemplateContentControlLocationEnum, Set<String>> allContentControlSet;
        protected Boolean flagFindAllContentControl;

        protected TemplateProperty() {
            templateActionMap = Collections.synchronizedMap(new HashMap<>());
            flagFindAllContentControl = true;
        }

    }

    private class TemplateAction {
        protected String type;
        protected final Set<String> excludedContentControlSet;
        protected final Set<String> includedContentControlSet;
        protected boolean flagInsertAll;

        protected TemplateAction() {
            excludedContentControlSet = Collections.synchronizedSet(new HashSet<>());
            includedContentControlSet = Collections.synchronizedSet(new HashSet<>());
            flagInsertAll = false;
        }
    }

    private TemplateConfig() {
        super();

        templatePropertiesMap = Collections.synchronizedMap(new HashMap<>());
    }

    public static TemplateConfig getInstance() {
        synchronized (firstInit) {
            if (config == null && firstInit) {
                config = new TemplateConfig();
                config.init();
                firstInit = Boolean.FALSE;
            }
        }

        return config;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void init() {
        templatePropertiesMap.clear();

        try {
            FileUtil.processJsonFile(ProjectPathUtil.PROJECT_CONFIG_DIR, CONFIG_FILE_NAME, (jsonParser, params) -> {
                while (jsonParser.nextToken() != null) {
                    if (jsonParser.currentToken() != JsonToken.FIELD_NAME
                            || !KEY_OBJECTARRAY_TEMPLATES.equals(jsonParser.getText())) {
                        continue;
                    }

                    jsonParser.nextToken();
                    if (jsonParser.currentToken() == JsonToken.START_ARRAY) {
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {

                            if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
                                TemplateProperty templateProperty = new TemplateProperty();

                                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                    if (jsonParser.currentToken() == JsonToken.FIELD_NAME) {
                                        String fieldName = jsonParser.getText();
                                        jsonParser.nextToken();

                                        if (KEY_STRING_KEY.equals(fieldName)) {
                                            templateProperty.key = jsonParser.getValueAsString();
                                            continue;
                                        }
                                        if (KEY_STRING_FILENAME.equals(fieldName)) {
                                            templateProperty.filename = jsonParser.getValueAsString();
                                            continue;
                                        }

                                        if (KEY_OBJECTARRAY_ACTIONS.equals(fieldName)
                                                && jsonParser.currentToken() == JsonToken.START_ARRAY) {
                                            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                                if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
                                                    TemplateAction templateAction = processTemplateAction(jsonParser);
                                                    if (templateAction.type != null) {
                                                        templateProperty.templateActionMap.put(templateAction.type,
                                                                templateAction);
                                                    }
                                                }
                                            }
                                        }

                                    }

                                    if (templateProperty.key != null) {
                                        ((Map<String, TemplateProperty>) params[0]).put(templateProperty.key,
                                                templateProperty);
                                    }
                                }
                            }
                        }
                    }
                }
            }, templatePropertiesMap);
        } catch (IllegalArgumentException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private TemplateAction processTemplateAction(JsonParser jsonParser) throws IOException {
        TemplateAction templateAction = new TemplateAction();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            if (jsonParser.currentToken() == JsonToken.FIELD_NAME) {
                String fieldName = jsonParser.getText();
                jsonParser.nextToken();
                if (KEY_STRING_TYPE.equals(fieldName)) {
                    templateAction.type = jsonParser.getValueAsString();
                    continue;
                }
                if (KEY_STRINGARRAY_EXCLUDED_CONTENT_CONTROL_LIST.equals(fieldName)) {
                    processStringArray(jsonParser, templateAction.excludedContentControlSet);
                    continue;
                }
                if (KEY_STRINGARRAY_INCLUDED_CONTENT_CONTROL_LIST.equals(fieldName)) {
                    processStringArray(jsonParser, templateAction.includedContentControlSet);
                    continue;
                }
                if (KEY_BOOLEAN_FLAG_INSERT_ALL.equals(fieldName)) {
                    templateAction.flagInsertAll = jsonParser.getValueAsBoolean();
                }
            }
        }

        return templateAction;
    }

    private void processStringArray(JsonParser jsonParser, Set<String> stringSet) throws IOException {
        if (jsonParser.currentToken() == JsonToken.START_ARRAY) {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                stringSet.add(jsonParser.getValueAsString());
            }
        }
    }

    public String retrieveFilename(String templateKey) {
        TemplateProperty templateProperty = templatePropertiesMap.get(templateKey);
        if (templateProperty == null || StringUtils.isBlank(templateProperty.filename)) {
            return StringUtils.EMPTY;
        }

        return templateProperty.filename;
    }

    public Set<String> retrieveIncludedContentControlTag(String templateKey, TemplateContentControlLocationEnum loc,
            String action) {
        TemplateProperty templateProperty = templatePropertiesMap.get(templateKey);

        if (templateProperty == null || templateProperty.templateActionMap.get(action) == null) {
            return Collections.emptySet();
        }

        TemplateAction templateAction = templateProperty.templateActionMap.get(action);
        Set<String> includedContentControlSet = new HashSet<>();

        synchronized (templateProperty.flagFindAllContentControl) {
            if (templateProperty.allContentControlSet == null && templateProperty.flagFindAllContentControl) {
                templateProperty.allContentControlSet = Collections.synchronizedMap(new HashMap<>());

                if (StringUtils.isNotBlank(templateProperty.filename)) {
                    WordprocessingMLPackage wordprocessingMLPackage = TemplateUtil
                            .retrieveWordprocessingMLPackage(ProjectPathUtil.TEMPLATE_DIR, templateProperty.filename);
                    if (wordprocessingMLPackage != null) {
                        for (TemplateContentControlLocationEnum location : TemplateContentControlLocationEnum
                                .values()) {
                            Set<String> tags = Collections.synchronizedSet(new HashSet<>());
                            tags.addAll(
                                    TemplateUtil
                                            .retrieveAllSdtElements(TemplateContentControlLocationEnum
                                                    .retrieveContentAccessor(location, wordprocessingMLPackage))
                                            .keySet());
                            tags.addAll(
                                    TemplateUtil
                                            .retrieveAllTbls(TemplateContentControlLocationEnum
                                                    .retrieveContentAccessor(location, wordprocessingMLPackage))
                                            .keySet());
                            templateProperty.allContentControlSet.put(location, tags);
                        }
                    }
                }
                templateProperty.flagFindAllContentControl = false;
            }
        }

        if (templateAction.flagInsertAll && templateProperty.allContentControlSet.get(loc) != null) {
            includedContentControlSet.addAll(templateProperty.allContentControlSet.get(loc));
        }

        includedContentControlSet.addAll(templateAction.includedContentControlSet);
        includedContentControlSet.removeAll(templateAction.excludedContentControlSet);

        return includedContentControlSet;
    }

    public Set<String> retrieveExcludedContentControlTag(String templateKey, String action) {
        TemplateProperty templateProperty = templatePropertiesMap.get(templateKey);

        if (templateProperty == null || templateProperty.templateActionMap.get(action) == null) {
            return Collections.emptySet();
        }

        return templateProperty.templateActionMap.get(action).excludedContentControlSet;
    }
}

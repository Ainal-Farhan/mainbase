package mainbase.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mainbase.functional.TemplateContentControlMethod;
import mainbase.functional.TemplateProcessMethod;
import mainbase.util.docx4j.TemplateContentControlMethodUtil;

public class TemplateConstant {
    public static final Map<String, TemplateProcessMethod> TEMPLATE_PROCESS_METHOD_MAP;
    public static final Map<String, TemplateContentControlMethod> WORD_CONTENT_CONTROL_METHOD;

    public static final String KEY_TEMPLATE_EXAMPLE_1 = "Test-Template.docx";
    public static final String KEY_TEMPLATE_EXAMPLE_TABLE_CC = "Test-Template2.docx";

    public static final String TAG_CC_EXAMPLE_TABLE_1 = "testTableWithHeader";
    public static final String TAG_CC_EXAMPLE_TABLE_2 = "testTableWithoutHeader";
    public static final String TAG_CC_EXAMPLE_TEXT_1 = "testText";
    public static final String TAG_CC_EXAMPLE_TEXT_2 = "testText2";
    public static final String TAG_CC_EXAMPLE_IMAGE_1 = "testImage";
    public static final String TAG_CC_EXAMPLE_EXT_TABLE_1 = "testExternalHeader";

    public static final String DEFAULT_HEADER_FOOTER_TYPE = "DEFAULT";
    public static final String EVEN_HEADER_FOOTER_TYPE = "EVEN";
    public static final String FIRST_HEADER_FOOTER_TYPE = "ODD";

    public static final String CREATE_TEMPLATE_ACTION = "CREATE";

    static {
        Map<String, TemplateProcessMethod> templateProcessMethodMap = new HashMap<>();
        templateProcessMethodMap.put(KEY_TEMPLATE_EXAMPLE_1, TemplateContentControlMethodUtil::processExample1);
        templateProcessMethodMap.put(KEY_TEMPLATE_EXAMPLE_TABLE_CC, TemplateContentControlMethodUtil::processExample1);
        TEMPLATE_PROCESS_METHOD_MAP = Collections.unmodifiableMap(templateProcessMethodMap);

        Map<String, TemplateContentControlMethod> wordContentControlMethod = new HashMap<>();
        wordContentControlMethod.put(TAG_CC_EXAMPLE_TABLE_1, TemplateContentControlMethodUtil.processExample1TableCC);
        wordContentControlMethod.put(TAG_CC_EXAMPLE_TABLE_2, TemplateContentControlMethodUtil.processExample2TableCC);
        wordContentControlMethod.put(TAG_CC_EXAMPLE_TEXT_1, TemplateContentControlMethodUtil.processExample3CC);
        wordContentControlMethod.put(TAG_CC_EXAMPLE_IMAGE_1, TemplateContentControlMethodUtil.processExample4ImageCC);
        wordContentControlMethod.put(TAG_CC_EXAMPLE_TEXT_2, TemplateContentControlMethodUtil.processExample5CC);
        wordContentControlMethod.put(TAG_CC_EXAMPLE_EXT_TABLE_1,
                TemplateContentControlMethodUtil.processExample1ExternalTableCC);

        WORD_CONTENT_CONTROL_METHOD = Collections.unmodifiableMap(wordContentControlMethod);
    }
}

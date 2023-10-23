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

	public static final String TEMPLATE_EXAMPLE_1 = "Test-Template.docx";

	static {
		Map<String, TemplateProcessMethod> templateProcessMethodMap = new HashMap<>();
		templateProcessMethodMap.put(TEMPLATE_EXAMPLE_1, TemplateContentControlMethodUtil::processExample1);
		TEMPLATE_PROCESS_METHOD_MAP = Collections.unmodifiableMap(templateProcessMethodMap);

		Map<String, TemplateContentControlMethod> wordContentControlMethod = new HashMap<>();
		WORD_CONTENT_CONTROL_METHOD = Collections.unmodifiableMap(wordContentControlMethod);
	}
}

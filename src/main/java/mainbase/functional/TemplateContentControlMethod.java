package mainbase.functional;

import mainbase.functional.parameter.TemplateContentControlMethodParameter;

@FunctionalInterface
public interface TemplateContentControlMethod {
	Object process(TemplateContentControlMethodParameter parameter);
}

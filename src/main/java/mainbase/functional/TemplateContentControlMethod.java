package mainbase.functional;

import mainbase.functional.parameter.TemplateContentControlMethodParameter;

@FunctionalInterface
public interface TemplateContentControlMethod {
    void process(final TemplateContentControlMethodParameter parameter);
}

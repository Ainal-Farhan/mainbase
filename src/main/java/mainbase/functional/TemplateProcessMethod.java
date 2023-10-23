package mainbase.functional;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

@FunctionalInterface
public interface TemplateProcessMethod {
	WordprocessingMLPackage process(WordprocessingMLPackage WordprocessingMLPackage);
}

package mainbase.enums;

import java.util.Arrays;
import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;

import mainbase.util.docx4j.TemplateUtil;

public enum TemplateContentControlLocationEnum {
    DEFAULT_HEADER, EVEN_HEADER, FIRST_HEADER, DEFAULT_FOOTER, EVEN_FOOTER, FIRST_FOOTER, BODY;

    public static List<? extends ContentAccessor> retrieveContentAccessor(TemplateContentControlLocationEnum location,
            WordprocessingMLPackage wordprocessingMLPackage) {

        if (wordprocessingMLPackage == null) {
            return Arrays.asList();
        }

        switch (location) {
        case BODY:
            return Arrays.asList(wordprocessingMLPackage.getMainDocumentPart());
        case DEFAULT_HEADER:
        case EVEN_HEADER:
        case FIRST_HEADER:
            return TemplateUtil.retrieveAllHeadersSeperately(wordprocessingMLPackage, location);
        case DEFAULT_FOOTER:
        case EVEN_FOOTER:
        case FIRST_FOOTER:
            return TemplateUtil.retrieveAllFootersSeperately(wordprocessingMLPackage, location);
        }

        return Arrays.asList();
    }
}

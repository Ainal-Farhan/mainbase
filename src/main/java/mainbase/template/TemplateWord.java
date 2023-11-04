package mainbase.template;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ContentAccessor;

import mainbase.enums.TemplateContentControlLocationEnum;
import mainbase.util.docx4j.TemplateUtil;

public class TemplateWord implements Serializable {

    private static final long serialVersionUID = 1L;

    private WordprocessingMLPackage wordprocessingMLPackage;

    private String refFilename;
    private String refFilePath;
    private String outputFilePath;
    private String outputFilename;
    private String templateKey;

    private Map<TemplateContentControlLocationEnum, List<? extends ContentAccessor>> contentAccessorsListMap;

    public TemplateWord(String refFilePath, String refFilename, String templateKey, String outputFilename,
            String outputFilePath) {
        this.refFilePath = refFilePath;
        this.refFilename = refFilename;
        this.outputFilePath = outputFilePath == null ? StringUtils.EMPTY : outputFilePath;
        this.outputFilename = outputFilename;
        this.templateKey = templateKey == null ? StringUtils.EMPTY : templateKey;
        wordprocessingMLPackage = TemplateUtil.retrieveWordprocessingMLPackage(refFilePath, refFilename);
        contentAccessorsListMap = new HashMap<>();

        if (wordprocessingMLPackage != null) {
            for (TemplateContentControlLocationEnum location : TemplateContentControlLocationEnum.values()) {
                contentAccessorsListMap.put(location,
                        TemplateContentControlLocationEnum.retrieveContentAccessor(location, wordprocessingMLPackage));
            }
        }
    }

    public void synchronizedTheContentAccessorsList() {
        Map<TemplateContentControlLocationEnum, List<? extends ContentAccessor>> contentAccessorsListMap = new HashMap<>();

        this.contentAccessorsListMap.forEach((location, contentAccessorsList) -> {
            contentAccessorsListMap.put(location, contentAccessorsList);
        });

        wordprocessingMLPackage.getMainDocumentPart().setJAXBContext(
                ((MainDocumentPart) contentAccessorsListMap.get(TemplateContentControlLocationEnum.BODY).get(0))
                        .getJAXBContext());

        for (SectionWrapper section : wordprocessingMLPackage.getDocumentModel().getSections()) {
            if (section.getHeaderFooterPolicy() == null) {
                continue;
            }

            if (section.getHeaderFooterPolicy().getDefaultHeader() != null
                    && contentAccessorsListMap.get(TemplateContentControlLocationEnum.DEFAULT_HEADER) != null
                    && !contentAccessorsListMap.get(TemplateContentControlLocationEnum.DEFAULT_HEADER).isEmpty()) {
                section.getHeaderFooterPolicy().getDefaultHeader().setJaxbElement(((HeaderPart) contentAccessorsListMap
                        .get(TemplateContentControlLocationEnum.DEFAULT_HEADER).get(0)).getJaxbElement());
                contentAccessorsListMap.get(TemplateContentControlLocationEnum.DEFAULT_HEADER).remove(0);
            }

            if (section.getHeaderFooterPolicy().getEvenHeader() != null
                    && contentAccessorsListMap.get(TemplateContentControlLocationEnum.EVEN_HEADER) != null
                    && !contentAccessorsListMap.get(TemplateContentControlLocationEnum.EVEN_HEADER).isEmpty()) {
                section.getHeaderFooterPolicy().getEvenHeader().setJaxbElement(((HeaderPart) contentAccessorsListMap
                        .get(TemplateContentControlLocationEnum.EVEN_HEADER).get(0)).getJaxbElement());
                contentAccessorsListMap.get(TemplateContentControlLocationEnum.EVEN_HEADER).remove(0);
            }

            if (section.getHeaderFooterPolicy().getFirstHeader() != null
                    && contentAccessorsListMap.get(TemplateContentControlLocationEnum.FIRST_HEADER) != null
                    && !contentAccessorsListMap.get(TemplateContentControlLocationEnum.FIRST_HEADER).isEmpty()) {
                section.getHeaderFooterPolicy().getFirstHeader().setJaxbElement(((HeaderPart) contentAccessorsListMap
                        .get(TemplateContentControlLocationEnum.FIRST_HEADER).get(0)).getJaxbElement());
                contentAccessorsListMap.get(TemplateContentControlLocationEnum.FIRST_HEADER).remove(0);
            }

            if (section.getHeaderFooterPolicy().getDefaultFooter() != null
                    && contentAccessorsListMap.get(TemplateContentControlLocationEnum.DEFAULT_FOOTER) != null
                    && !contentAccessorsListMap.get(TemplateContentControlLocationEnum.DEFAULT_FOOTER).isEmpty()) {
                section.getHeaderFooterPolicy().getDefaultFooter().setJaxbElement(((FooterPart) contentAccessorsListMap
                        .get(TemplateContentControlLocationEnum.DEFAULT_FOOTER).get(0)).getJaxbElement());
                contentAccessorsListMap.get(TemplateContentControlLocationEnum.DEFAULT_FOOTER).remove(0);
            }

            if (section.getHeaderFooterPolicy().getEvenFooter() != null
                    && contentAccessorsListMap.get(TemplateContentControlLocationEnum.EVEN_FOOTER) != null
                    && !contentAccessorsListMap.get(TemplateContentControlLocationEnum.EVEN_FOOTER).isEmpty()) {
                section.getHeaderFooterPolicy().getEvenFooter().setJaxbElement(((FooterPart) contentAccessorsListMap
                        .get(TemplateContentControlLocationEnum.EVEN_FOOTER).get(0)).getJaxbElement());
                contentAccessorsListMap.get(TemplateContentControlLocationEnum.EVEN_FOOTER).remove(0);
            }

            if (section.getHeaderFooterPolicy().getFirstFooter() != null
                    && contentAccessorsListMap.get(TemplateContentControlLocationEnum.FIRST_FOOTER) != null
                    && !contentAccessorsListMap.get(TemplateContentControlLocationEnum.FIRST_FOOTER).isEmpty()) {
                section.getHeaderFooterPolicy().getFirstFooter().setJaxbElement(((FooterPart) contentAccessorsListMap
                        .get(TemplateContentControlLocationEnum.FIRST_FOOTER).get(0)).getJaxbElement());
                contentAccessorsListMap.get(TemplateContentControlLocationEnum.FIRST_FOOTER).remove(0);
            }
        }
    }

    public WordprocessingMLPackage getWordprocessingMLPackage() {
        return wordprocessingMLPackage;
    }

    public void setWordprocessingMLPackage(WordprocessingMLPackage wordprocessingMLPackage) {
        this.wordprocessingMLPackage = wordprocessingMLPackage;
    }

    public Map<TemplateContentControlLocationEnum, List<? extends ContentAccessor>> getContentAccessorsListMap() {
        return contentAccessorsListMap;
    }

    public void setContentAccessorsListMap(
            Map<TemplateContentControlLocationEnum, List<? extends ContentAccessor>> contentAccessorsListMap) {
        this.contentAccessorsListMap = contentAccessorsListMap;
    }

    public String getTemplateKey() {
        return templateKey;
    }

    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }

    public String getRefFilename() {
        return refFilename;
    }

    public void setRefFilename(String refFilename) {
        this.refFilename = refFilename;
    }

    public String getRefFilePath() {
        return refFilePath;
    }

    public void setRefFilePath(String refFilePath) {
        this.refFilePath = refFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }
}

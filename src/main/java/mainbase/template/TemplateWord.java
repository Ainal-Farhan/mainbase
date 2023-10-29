package mainbase.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.docx4j.TraversalUtil;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.SdtElement;
import org.docx4j.wml.SdtPr;
import org.docx4j.wml.Tag;

import mainbase.enums.TemplateContentControlLocationEnum;
import mainbase.util.docx4j.TemplateUtil;
import mainbase.util.path.ProjectPathUtil;

public class TemplateWord implements Serializable {

    private static final long serialVersionUID = 1L;

    private WordprocessingMLPackage wordprocessingMLPackage;

    private String filename;

    private Map<TemplateContentControlLocationEnum, Set<String>> allTagSet;
    private Map<TemplateContentControlLocationEnum, List<? extends ContentAccessor>> contentAccessorsListMap;

    public TemplateWord(String filename) {
        this.filename = filename;
        wordprocessingMLPackage = TemplateUtil.retrieveWordprocessingMLPackage(ProjectPathUtil.TEMPLATE_DIR, filename);
        allTagSet = new HashMap<>();
        contentAccessorsListMap = new HashMap<>();

        if (wordprocessingMLPackage != null) {
            for (TemplateContentControlLocationEnum location : TemplateContentControlLocationEnum.values()) {
                contentAccessorsListMap.put(location,
                        TemplateContentControlLocationEnum.retrieveContentAccessor(location, wordprocessingMLPackage));
                Set<String> tags = new HashSet<>();
                tags.addAll(TemplateUtil.retrieveAllSdtElements(contentAccessorsListMap.get(location)).keySet());
                tags.addAll(TemplateUtil.retrieveAllTbls(contentAccessorsListMap.get(location)).keySet());
                allTagSet.put(location, tags);
            }
        }
    }

    public class SdtElementFinder extends TraversalUtil.CallbackImpl {
        private Map<String, List<SdtElement>> sdtElementsByTag = new HashMap<>();

        @Override
        public List<Object> apply(Object o) {
            if (o instanceof SdtElement) {
                SdtElement sdtElement = (SdtElement) o;

                // Check for the presence of a tag and categorize by tag value
                SdtPr sdtPr = sdtElement.getSdtPr();
                if (sdtPr != null) {
                    Tag tag = sdtPr.getTag();
                    if (tag != null && tag.getVal() != null) {
                        String tagValue = tag.getVal();
                        sdtElementsByTag.computeIfAbsent(tagValue, k -> new ArrayList<>()).add(sdtElement);
                    }
                }
            }
            return null;
        }

        public Map<String, List<SdtElement>> getSdtElementsByTag() {
            return sdtElementsByTag;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Map<TemplateContentControlLocationEnum, Set<String>> getAllTagSet() {
        return allTagSet;
    }

    public void setAllTagSet(Map<TemplateContentControlLocationEnum, Set<String>> allTagSet) {
        this.allTagSet = allTagSet;
    }

    public Map<TemplateContentControlLocationEnum, List<? extends ContentAccessor>> getContentAccessorsListMap() {
        return contentAccessorsListMap;
    }

    public void setContentAccessorsListMap(
            Map<TemplateContentControlLocationEnum, List<? extends ContentAccessor>> contentAccessorsListMap) {
        this.contentAccessorsListMap = contentAccessorsListMap;
    }
}

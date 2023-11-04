package mainbase.util.docx4j;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.wml.ContentAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.docx4j.wml.SdtElement;
import org.docx4j.wml.SdtPr;
import org.docx4j.wml.Tbl;

import jakarta.xml.bind.JAXBElement;
import mainbase.config.TemplateConfig;
import mainbase.constant.TemplateConstant;
import mainbase.enums.TemplateContentControlLocationEnum;
import mainbase.functional.TemplateProcessMethod;
import mainbase.template.TemplateContentControl;
import mainbase.template.TemplateWord;
import mainbase.util.file.FileValidatorUtil;
import mainbase.util.path.ProjectPathUtil;

public class TemplateUtil {
    protected static Logger log = LoggerFactory.getLogger(TemplateContentControlUtil.class);

    /**
     * This is the main method for processing multiple documents (docx)
     * concurrently
     * 
     * @param templateKeyMap
     *            This cannot be null.
     * @param templateWordMap
     *            This can be null or empty if all of the documents need to be
     *            referred from the resource templates.
     * @param numThreads
     *            Please set the number of thread required.
     */
    public static void processMultipleTemplateConcurrently(Map<String, String> templateKeyMap,
            Map<String, TemplateWord> templateWordMap, int numThreads) {
        if (templateKeyMap == null || templateKeyMap.isEmpty() || numThreads <= 0) {
            return;
        }

        if (templateWordMap == null) {
            templateWordMap = Collections.emptyMap();
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (Entry<String, String> entry : templateKeyMap.entrySet()) {
            TemplateWord templateWord = templateWordMap.get(entry.getKey());
            executor.submit(() -> {
                try {
                    log.info("Start processing template: " + entry.getKey() + " (" + entry.getValue() + ")");
                    processTemplate(entry.getKey(), entry.getValue(), templateWord);
                } catch (Exception e) {
                    log.error("Error processing template: " + entry.getKey() + " (" + entry.getValue() + ")", e);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            log.error("Thread execution was interrupted.", e);
        }
    }

    /**
     * This is the main method for processing a document (docx)
     * 
     * @param keyTemplate
     *            This cannot be null.
     * @param action
     *            This cannot be null.
     * @param templateWord
     *            Please set the value to null if you want to use the resource
     *            template.
     */
    public static void processTemplate(String keyTemplate, String action, TemplateWord templateWord) {
        if (StringUtils.isBlank(keyTemplate)) {
            return;
        }

        if (templateWord == null) {
            String filename = TemplateConfig.getInstance().retrieveFilename(keyTemplate);
            if (StringUtils.isBlank(filename)) {
                return;
            }
            templateWord = new TemplateWord(ProjectPathUtil.TEMPLATE_DIR, filename, keyTemplate, StringUtils.EMPTY);
        }

        templateWord.setTemplateKey(keyTemplate);

        processTemplate(templateWord, action, TemplateConstant.TEMPLATE_PROCESS_METHOD_MAP.get(keyTemplate));
    }

    private static void processTemplate(TemplateWord templateWord, String action,
            TemplateProcessMethod docx4jProcessMethod) {
        if (templateWord == null || templateWord.getWordprocessingMLPackage() == null
                || StringUtils.isBlank(templateWord.getTemplateKey())) {
            return;
        }

        Map<String, TemplateContentControl> contentControlMap = new HashMap<>();

        templateWord.getContentAccessorsListMap().forEach((locationEnum, contentAccessor) -> {
            TemplateConfig.getInstance()
                    .retrieveIncludedContentControlTag(templateWord.getTemplateKey(), locationEnum, action).stream()
                    .forEach(tag -> {
                        TemplateContentControl contentControl = contentControlMap.get(tag);
                        if (contentControl == null) {
                            contentControl = new TemplateContentControl(tag);
                        }

                        Set<TemplateContentControlLocationEnum> locationList = new HashSet<>();

                        if (contentControl.getLocationList() != null) {
                            locationList.addAll(contentControl.getLocationList());
                        }

                        locationList.add(locationEnum);
                        contentControl.setLocationList(new ArrayList<>(locationList));

                        contentControlMap.put(tag, contentControl);
                    });
        });

        if (contentControlMap.values() != null && !contentControlMap.values().isEmpty()) {
            TemplateContentControlUtil.processContentControlAndInsert(new ArrayList<>(contentControlMap.values()),
                    templateWord, action);
        }

        templateWord.synchronizedTheContentAccessorsList();
        if (docx4jProcessMethod != null) {
            templateWord
                    .setWordprocessingMLPackage(docx4jProcessMethod.process(templateWord.getWordprocessingMLPackage()));
        }

        if (templateWord.getWordprocessingMLPackage() != null) {
            try {
                templateWord.getWordprocessingMLPackage().save(new File(
                        ProjectPathUtil.TEMPLATE_OUTPUT_DIR + templateWord.getOutputDir(), templateWord.getFilename()));
            } catch (Docx4JException e) {
            }
        }
    }

    public static List<HeaderPart> retrieveAllHeadersSeperately(WordprocessingMLPackage wordprocessingMLPackage,
            TemplateContentControlLocationEnum location) {
        List<HeaderPart> headerParts = new ArrayList<>();
        if (wordprocessingMLPackage == null || location == null) {
            return headerParts;
        }

        for (SectionWrapper section : wordprocessingMLPackage.getDocumentModel().getSections()) {
            if (section.getHeaderFooterPolicy() == null) {
                continue;
            }

            switch (location) {
            case DEFAULT_HEADER:
                if (section.getHeaderFooterPolicy().getDefaultHeader() != null) {
                    headerParts.add(section.getHeaderFooterPolicy().getDefaultHeader());
                }
                continue;
            case EVEN_HEADER:
                if (section.getHeaderFooterPolicy().getEvenHeader() != null) {
                    headerParts.add(section.getHeaderFooterPolicy().getEvenHeader());
                }
                continue;
            case FIRST_HEADER:
                if (section.getHeaderFooterPolicy().getFirstHeader() != null) {
                    headerParts.add(section.getHeaderFooterPolicy().getFirstHeader());
                }
                continue;
            default:
                break;
            }
        }

        return headerParts;
    }

    public static List<FooterPart> retrieveAllFootersSeperately(WordprocessingMLPackage wordprocessingMLPackage,
            TemplateContentControlLocationEnum location) {
        List<FooterPart> footerParts = new ArrayList<>();
        if (wordprocessingMLPackage == null || location == null) {
            return footerParts;
        }

        for (SectionWrapper section : wordprocessingMLPackage.getDocumentModel().getSections()) {
            if (section.getHeaderFooterPolicy() == null) {
                continue;
            }

            switch (location) {
            case DEFAULT_FOOTER:
                if (section.getHeaderFooterPolicy().getDefaultFooter() != null) {
                    footerParts.add(section.getHeaderFooterPolicy().getDefaultFooter());
                }
                continue;
            case EVEN_FOOTER:

                if (section.getHeaderFooterPolicy().getEvenFooter() != null) {
                    footerParts.add(section.getHeaderFooterPolicy().getEvenFooter());
                }
                continue;
            case FIRST_FOOTER:

                if (section.getHeaderFooterPolicy().getFirstFooter() != null) {
                    footerParts.add(section.getHeaderFooterPolicy().getFirstFooter());
                }
                continue;
            default:
                break;
            }
        }

        return footerParts;
    }

    public static Map<String, List<SdtElement>> retrieveAllSdtElements(List<? extends ContentAccessor> partList) {
        Map<String, List<SdtElement>> sdtElementsListMap = new HashMap<>();
        partList.stream().forEach(contentAccessor -> {
            if (contentAccessor != null) {
                Method getJaxbElementMethod = null;
                Object jaxbElement = null;
                try {
                    getJaxbElementMethod = contentAccessor.getClass().getMethod("getJaxbElement");
                    if (getJaxbElementMethod != null) {
                        jaxbElement = getJaxbElementMethod.invoke(contentAccessor);
                    }
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                }
                if (jaxbElement != null) {
                    SdtElementFinder sdtFinder = new SdtElementFinder();
                    sdtFinder.walkJAXBElements(jaxbElement);
                    sdtFinder.getSdtElementsByTag().forEach((tag, sdtElementList) -> {
                        sdtElementsListMap.computeIfAbsent(tag, k -> new ArrayList<>()).addAll(sdtElementList);
                    });
                } else {
                    retrieveAllSdtElementsRecursive(partList).forEach((tag, sdtElementList) -> {
                        sdtElementsListMap.computeIfAbsent(tag, k -> new ArrayList<>()).addAll(sdtElementList);
                    });
                }
            }
        });
        return sdtElementsListMap;
    }

    public static Map<String, List<Tbl>> retrieveAllTbls(List<? extends ContentAccessor> partList) {
        Map<String, List<Tbl>> tblListMap = new HashMap<>();
        partList.stream().forEach(contentAccessor -> {
            if (contentAccessor != null) {
                Method getJaxbElementMethod = null;
                Object jaxbElement = null;
                try {
                    getJaxbElementMethod = contentAccessor.getClass().getMethod("getJaxbElement");
                    if (getJaxbElementMethod != null) {
                        jaxbElement = getJaxbElementMethod.invoke(contentAccessor);
                    }
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                }

                if (jaxbElement != null) {
                    TblFinder tblFinder = new TblFinder();
                    tblFinder.walkJAXBElements(jaxbElement);
                    tblFinder.getTblsByTag().forEach((tag, tblList) -> {
                        tblListMap.computeIfAbsent(tag, k -> new ArrayList<>()).addAll(tblList);
                    });
                }
            }
        });
        return tblListMap;
    }

    public static Map<String, List<SdtElement>> retrieveAllSdtElementsRecursive(
            List<? extends ContentAccessor> partList) {
        Map<String, List<SdtElement>> sdtElementListMap = new HashMap<>();
        if (partList == null) {
            return sdtElementListMap;
        }

        for (ContentAccessor part : partList) {
            processContent(part, sdtElementListMap);
        }
        return sdtElementListMap;
    }

    private static void processContent(ContentAccessor contentAccessor,
            Map<String, List<SdtElement>> sdtElementListMap) {
        if (contentAccessor == null) {
            return;
        }

        for (Object content : contentAccessor.getContent()) {
            if (content instanceof JAXBElement<?>) {
                content = ((JAXBElement<?>) content).getValue();
            }

            if (content instanceof ContentAccessor) {
                processContent((ContentAccessor) content, sdtElementListMap);
            } else if (content instanceof SdtElement) {
                SdtElement sdtElement = (SdtElement) content;
                SdtPr sdtProperties = sdtElement.getSdtPr();
                if (sdtProperties != null) {
                    String tag = sdtProperties.getTag() != null ? sdtProperties.getTag().getVal() : null;
                    if (StringUtils.isNotBlank(tag)) {
                        sdtElementListMap.computeIfAbsent(tag, k -> new ArrayList<>()).add(sdtElement);
                    }
                }
            }
        }
    }

    public static Tbl findNearestTable(Object obj) {
        if (obj == null) {
            return null;
        }

        Method getParentMethod = null;
        Object parent = null;
        try {
            getParentMethod = obj != null ? obj.getClass().getMethod("getParent") : null;
            if (getParentMethod == null) {
                return null;
            }
            parent = getParentMethod.invoke(obj);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
        }

        if (parent == null) {
            return null;
        }

        if (parent instanceof Tbl) {
            return (Tbl) parent;
        }

        return findNearestTable(parent);
    }

    public static WordprocessingMLPackage retrieveWordprocessingMLPackage(String filePath, String fileName) {
        try {
            return FileValidatorUtil.isValidDocx(filePath, fileName)
                    ? WordprocessingMLPackage.load(new File(filePath, fileName))
                    : null;
        } catch (Docx4JException e) {
        }

        return null;
    }

}

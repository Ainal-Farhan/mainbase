package mainbase.util.docx4j;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.wml.JcEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mainbase.config.TemplateConfig;
import mainbase.constant.TemplateConstant;
import mainbase.functional.TemplateContentControlMethod;
import mainbase.functional.parameter.TemplateContentControlMethodParameter;
import mainbase.template.TemplateContentControl;
import mainbase.template.TemplateContentControlTable;
import mainbase.template.TemplateWord;

public class TemplateContentControlUtil {
    protected static Logger log = LoggerFactory.getLogger(TemplateContentControlUtil.class);

    private final static int DEFAULT_IMAGE_HEIGHT = 100;
    private final static int DEFAULT_IMAGE_WIDTH = 100;
    private final static JcEnumeration DEFAULT_IMAGE_ALIGNMENT = JcEnumeration.CENTER;

    private static List<TemplateContentControl> filterPelupusanWordContentControlVOList(
            List<TemplateContentControl> contentControlList) {
        if (contentControlList == null || contentControlList.isEmpty()) {
            return contentControlList;
        }

        return contentControlList.stream().filter(cc -> StringUtils.isNotBlank(cc.getTag())
                && cc.getLocationList() != null && !cc.getLocationList().isEmpty()).collect(Collectors.toList());
    }

    public static List<TemplateContentControl> processWordContentControl(
            List<TemplateContentControl> contentControlList) {
        contentControlList = filterPelupusanWordContentControlVOList(contentControlList);

        if (contentControlList == null || contentControlList.isEmpty()) {
            return contentControlList;
        }

        final TemplateContentControlMethodParameter parameter = new TemplateContentControlMethodParameter();

        contentControlList.stream().forEach(cc -> {
            TemplateContentControlMethod method = TemplateConstant.WORD_CONTENT_CONTROL_METHOD.get(cc.getTag());
            if (method != null) {
                parameter.contentControl = cc;
                method.process(parameter);
                cc = parameter.contentControl;
            }
        });

        return contentControlList;
    }

    public static void processContentControlAndInsert(List<TemplateContentControl> contentControlList,
            TemplateWord templateWord, String action) {
        contentControlList = filterPelupusanWordContentControlVOList(contentControlList);

        if (contentControlList == null || contentControlList.isEmpty()
                || templateWord.getWordprocessingMLPackage() == null || StringUtils.isBlank(action)) {
            return;
        }

        List<TemplateContentControl> ccVOProcessList = contentControlList.stream()
                .filter(cc -> cc.getFlagProcess() == null || cc.getFlagProcess()).collect(Collectors.toList());
        List<TemplateContentControl> ccVONotProcessList = contentControlList.stream()
                .filter(cc -> cc.getFlagProcess() != null && !cc.getFlagProcess()).collect(Collectors.toList());

        ccVOProcessList = processWordContentControl(ccVOProcessList);

        ccVOProcessList.addAll(ccVONotProcessList);

        ccVOProcessList.stream().forEach(cc -> {
            if (cc.getType() != null) {
                switch (cc.getType()) {
                case TEXT:
                    if (cc.getValue() != null && cc.getValue() instanceof String) {
                        cc.getLocationList().stream().forEach(loc -> {
                            if (TemplateConfig.getInstance().retrieveIncludedContentControlTag(
                                    templateWord.getTemplateKey(), loc, action) != null
                                    && !TemplateConfig.getInstance().retrieveIncludedContentControlTag(
                                            templateWord.getTemplateKey(), loc, action).isEmpty()) {
                                InsertContentControlUtil.insertValueForContentControlIntoTemplate(
                                        templateWord.getContentAccessorsListMap().get(loc), cc.getTag(),
                                        (String) cc.getValue());
                            }
                        });
                    }
                    break;
                case IMAGE:
                    if (cc.getValue() != null && cc.getValue() instanceof byte[]) {
                        cc.getLocationList().stream().forEach(loc -> {
                            if (TemplateConfig.getInstance().retrieveIncludedContentControlTag(
                                    templateWord.getTemplateKey(), loc, action) != null
                                    && !TemplateConfig.getInstance().retrieveIncludedContentControlTag(
                                            templateWord.getTemplateKey(), loc, action).isEmpty()) {
                                InsertContentControlUtil.insertImageForContentControlIntoTemplate(
                                        templateWord.getWordprocessingMLPackage(),
                                        templateWord.getContentAccessorsListMap().get(loc), (byte[]) cc.getValue(),
                                        cc.getImageWidth() == null ? DEFAULT_IMAGE_WIDTH : cc.getImageWidth(),
                                        cc.getImageHeight() == null ? DEFAULT_IMAGE_HEIGHT : cc.getImageHeight(),
                                        cc.getImageAlignment() == null ? DEFAULT_IMAGE_ALIGNMENT
                                                : cc.getImageAlignment(),
                                        cc.getTag());
                            }
                        });
                    }
                    break;
                case TABLE:
                    TemplateContentControlTable value = cc.retrieveValueAs(TemplateContentControlTable.class);
                    if (value != null) {
                        cc.getLocationList().stream().forEach(loc -> {
                            if (TemplateConfig.getInstance().retrieveIncludedContentControlTag(
                                    templateWord.getTemplateKey(), loc, action) != null
                                    && !TemplateConfig.getInstance().retrieveIncludedContentControlTag(
                                            templateWord.getTemplateKey(), loc, action).isEmpty()) {
                                InsertContentControlUtil.insertContentControlTableIntoTemplate(value,
                                        templateWord.getWordprocessingMLPackage(), loc);
                            }
                        });
                    }
                }
            }
        });
    }
}

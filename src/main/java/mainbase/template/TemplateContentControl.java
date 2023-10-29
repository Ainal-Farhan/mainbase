package mainbase.template;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.wml.JcEnumeration;

import mainbase.enums.TemplateContentControlLocationEnum;
import mainbase.enums.TemplateContentControlTypeEnum;
import mainbase.exception.TemplateContentControlException;

public class TemplateContentControl implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tag;
    private List<TemplateContentControlLocationEnum> locationList;
    private TemplateContentControlTypeEnum type;
    private Object value;
    private Boolean flagProcess;

    private JcEnumeration imageAlignment;
    private Integer imageHeight;
    private Integer imageWidth;

    public TemplateContentControl(String tag) {
        if (StringUtils.isBlank(tag)) {
            throw new TemplateContentControlException("No tag is defined for this content control");
        }

        this.tag = tag;
    }

    public TemplateContentControl(String tag, List<TemplateContentControlLocationEnum> locationList,
            TemplateContentControlTypeEnum type) {
        initText(tag, locationList, type, Boolean.TRUE, null);
    }

    public TemplateContentControl(String tag, TemplateContentControlLocationEnum location,
            TemplateContentControlTypeEnum type) {
        initText(tag, Arrays.asList(location), type, Boolean.TRUE, null);
    }

    public TemplateContentControl(String tag, List<TemplateContentControlLocationEnum> locationList,
            TemplateContentControlTypeEnum type, Boolean flagProcess, Object value) {
        initText(tag, locationList, type, flagProcess == null ? Boolean.TRUE : flagProcess, value);
    }

    public TemplateContentControl(String tag, TemplateContentControlLocationEnum location,
            TemplateContentControlTypeEnum type, Boolean flagProcess, Object value) {
        initText(tag, Arrays.asList(location), type, flagProcess == null ? Boolean.TRUE : flagProcess, value);
    }

    public TemplateContentControl(String tag, List<TemplateContentControlLocationEnum> locationList,
            TemplateContentControlTypeEnum type, Integer imageWidth, Integer imageHeight,
            JcEnumeration imageAlignment) {
        initImage(tag, locationList, type, imageWidth, imageHeight, imageAlignment, Boolean.TRUE, null);
    }

    public TemplateContentControl(String tag, TemplateContentControlLocationEnum location,
            TemplateContentControlTypeEnum type, Integer imageWidth, Integer imageHeight,
            JcEnumeration imageAlignment) {
        initImage(tag, Arrays.asList(location), type, imageWidth, imageHeight, imageAlignment, Boolean.TRUE, null);
    }

    public TemplateContentControl(String tag, List<TemplateContentControlLocationEnum> locationList,
            TemplateContentControlTypeEnum type, Integer imageWidth, Integer imageHeight, JcEnumeration imageAlignment,
            Boolean flagProcess, Object value) {
        initImage(tag, locationList, type, imageWidth, imageHeight, imageAlignment,
                flagProcess == null ? Boolean.TRUE : flagProcess, value);
    }

    public TemplateContentControl(String tag, TemplateContentControlLocationEnum location,
            TemplateContentControlTypeEnum type, Integer imageWidth, Integer imageHeight, JcEnumeration imageAlignment,
            Boolean flagProcess, Object value) {
        initImage(tag, Arrays.asList(location), type, imageWidth, imageHeight, imageAlignment,
                flagProcess == null ? Boolean.TRUE : flagProcess, value);
    }

    private void initText(String tag, List<TemplateContentControlLocationEnum> locationList,
            TemplateContentControlTypeEnum type, Boolean flagProcess, Object value) {
        this.tag = tag;
        this.locationList = locationList;
        this.type = type;
        this.flagProcess = flagProcess;
        this.value = value;
    }

    private void initImage(String tag, List<TemplateContentControlLocationEnum> locationList,
            TemplateContentControlTypeEnum type, Integer imageWidth, Integer imageHeight, JcEnumeration imageAlignment,
            Boolean flagProcess, Object value) {
        this.tag = tag;
        this.locationList = locationList;
        this.type = type;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageAlignment = imageAlignment;
        this.flagProcess = flagProcess;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public <T> T retrieveValueAs(Class<T> clazz) {
        return value != null && value.getClass().equals(clazz) ? (T) value : null;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<TemplateContentControlLocationEnum> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<TemplateContentControlLocationEnum> locationList) {
        this.locationList = locationList;
    }

    public TemplateContentControlTypeEnum getType() {
        return type;
    }

    public void setType(TemplateContentControlTypeEnum type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public JcEnumeration getImageAlignment() {
        return imageAlignment;
    }

    public void setImageAlignment(JcEnumeration imageAlignment) {
        this.imageAlignment = imageAlignment;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Boolean getFlagProcess() {
        return flagProcess;
    }

    public void setFlagProcess(Boolean flagProcess) {
        this.flagProcess = flagProcess;
    }
}

package mainbase.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import mainbase.enums.TemplateContentControlLocationEnum;
import mainbase.exception.TemplateContentControlException;

public class TemplateContentControlTable implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Map<String, String>> rows;

    private boolean flagExternalResource;
    private boolean flagHasHeader;
    private boolean flagCopyManyRows;

    private String externalPath;
    private String externalFilename;
    private String externalTableTag;
    private TemplateContentControlLocationEnum externalLocationTable;

    private String tableTag;
    private Integer numHeaderRows;
    private Integer numCopiedRows;

    public TemplateContentControlTable(String tableTag) {
        if (StringUtils.isBlank(tableTag)) {
            throw new TemplateContentControlException("No Table Tag Found");
        }

        this.tableTag = tableTag;
        flagExternalResource = false;
        flagHasHeader = false;
        flagCopyManyRows = false;
        rows = new ArrayList<Map<String, String>>();
    }

    public boolean isFlagCopyManyRows() {
        return flagCopyManyRows;
    }

    public void setFlagCopyManyRows(boolean flagCopyManyRows) {
        this.flagCopyManyRows = flagCopyManyRows;
    }

    public Integer getNumCopiedRows() {
        return numCopiedRows;
    }

    public void setNumCopiedRows(Integer numCopiedRows) {
        this.numCopiedRows = numCopiedRows;
    }

    public List<Map<String, String>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, String>> rows) {
        this.rows = rows;
    }

    public String getExternalPath() {
        return externalPath;
    }

    public void setExternalPath(String externalPath) {
        this.externalPath = externalPath;
    }

    public String getTableTag() {
        return tableTag;
    }

    public void setTableTag(String tableTag) {
        this.tableTag = tableTag;
    }

    public boolean isFlagExternalResource() {
        return flagExternalResource;
    }

    public void setFlagExternalResource(boolean flagExternalResource) {
        this.flagExternalResource = flagExternalResource;
    }

    public boolean isFlagHasHeader() {
        return flagHasHeader;
    }

    public void setFlagHasHeader(boolean flagHasHeader) {
        this.flagHasHeader = flagHasHeader;
    }

    public boolean getFlagHasHeader() {
        return flagHasHeader;
    }

    public String getExternalTableTag() {
        return externalTableTag;
    }

    public void setExternalTableTag(String externalTableTag) {
        this.externalTableTag = externalTableTag;
    }

    public Integer getNumHeaderRows() {
        return numHeaderRows;
    }

    public void setNumHeaderRows(Integer numHeaderRows) {
        this.numHeaderRows = numHeaderRows;
    }

    public String getExternalFilename() {
        return externalFilename;
    }

    public void setExternalFilename(String externalFilename) {
        this.externalFilename = externalFilename;
    }

    public TemplateContentControlLocationEnum getExternalLocationTable() {
        return externalLocationTable;
    }

    public void setExternalLocationTable(TemplateContentControlLocationEnum externalLocationTable) {
        this.externalLocationTable = externalLocationTable;
    }
}

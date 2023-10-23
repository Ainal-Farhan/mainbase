package mainbase.template;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TemplateContentControlTable implements Serializable{

	private static final long serialVersionUID = 1L;

	private List<Map<String, String>> rows;

	private boolean flagExternalResource;
	private boolean flagHasHeader;
	private boolean flagCopyManyRows;

	private String externalPath;
	private String externalTableTag;

	private String tableTag;
	private Integer numHeaderRows;
	private Integer numCopiedRows;

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
}

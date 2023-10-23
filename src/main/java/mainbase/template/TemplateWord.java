package mainbase.template;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.wml.SdtElement;

import mainbase.enums.TemplateContentControlLocationEnum;
import mainbase.util.docx4j.TemplateUtil;
import mainbase.util.path.ProjectPathUtil;

public class TemplateWord implements Serializable {

	private static final long serialVersionUID = 1L;

	private WordprocessingMLPackage wordprocessingMLPackage;

	private String filename;

	private Map<TemplateContentControlLocationEnum, Map<String, List<SdtElement>>> sdtElementListMap;

	public TemplateWord(String filename) {
		this.filename = filename;
		wordprocessingMLPackage = TemplateUtil.retrieveWordprocessingMLPackage(ProjectPathUtil.TEMPLATE_DIR, filename);
		sdtElementListMap = new HashMap<>();

		if (wordprocessingMLPackage != null) {
			List<HeaderPart> headerParts = TemplateUtil.retrieveAllHeaders(wordprocessingMLPackage);
			List<FooterPart> footerParts = TemplateUtil.retrieveAllFooters(wordprocessingMLPackage);

			Map<String, List<SdtElement>> headerSdtElementListMap = TemplateUtil.retrieveAllSdtElements(headerParts);
			Map<String, List<SdtElement>> footerSdtElementListMap = TemplateUtil.retrieveAllSdtElements(footerParts);
			Map<String, List<SdtElement>> bodySdtElementListMap = TemplateUtil
					.retrieveAllSdtElements(Arrays.asList(wordprocessingMLPackage.getMainDocumentPart()));

			sdtElementListMap.put(TemplateContentControlLocationEnum.HEADER, headerSdtElementListMap);
			sdtElementListMap.put(TemplateContentControlLocationEnum.FOOTER, footerSdtElementListMap);
			sdtElementListMap.put(TemplateContentControlLocationEnum.BODY, bodySdtElementListMap);
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

	public Map<TemplateContentControlLocationEnum, Map<String, List<SdtElement>>> getSdtElementListMap() {
		return sdtElementListMap;
	}

	public void setSdtElementListMap(
			Map<TemplateContentControlLocationEnum, Map<String, List<SdtElement>>> sdtElementListMap) {
		this.sdtElementListMap = sdtElementListMap;
	}

}

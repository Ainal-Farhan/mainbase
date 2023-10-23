package mainbase.util.docx4j;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Spacing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.SdtBlock;
import org.docx4j.wml.SdtElement;
import org.docx4j.wml.SdtPr;
import org.docx4j.wml.SdtRun;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

import jakarta.xml.bind.JAXBElement;
import mainbase.constant.TemplateConstant;
import mainbase.enums.TemplateContentControlLocationEnum;
import mainbase.functional.TemplateProcessMethod;
import mainbase.template.TemplateContentControl;
import mainbase.template.TemplateContentControlTable;
import mainbase.template.TemplateWord;
import mainbase.util.file.FileValidatorUtil;
import mainbase.util.path.ProjectPathUtil;

public class TemplateUtil {
	protected static Logger log = LoggerFactory.getLogger(TemplateContentControlUtil.class);

	private static final String PRESERVE = "preserve";

	public static void processMultipleTemplateConcurrently(List<String> templateNameList) {
		if (templateNameList == null || templateNameList.isEmpty()) {
			return;
		}

		int numThreads = 4;
		List<Thread> threads = new ArrayList<>();
		int sublistSize = templateNameList.size() / numThreads;

		for (int i = 0; i < numThreads; i++) {
			int startIndex = i * sublistSize;
			int endIndex = (i == numThreads - 1) ? templateNameList.size() : (startIndex + sublistSize);

			List<String> sublist = templateNameList.subList(startIndex, endIndex);

			Thread thread = new Thread(
					() -> sublist.parallelStream().forEach(templateName -> processTemplate(templateName)));
			threads.add(thread);
			thread.start();
		}

		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
		}
	}

	public static void processTemplate(String templateName) {
		if (StringUtils.isBlank(templateName)) {
			return;
		}

		processTemplate(templateName, TemplateConstant.TEMPLATE_PROCESS_METHOD_MAP.get(templateName));
	}

	public static void processTemplate(String templateName, TemplateProcessMethod docx4jProcessMethod) {
		WordprocessingMLPackage wordprocessingMLPackage = retrieveWordprocessingMLPackage(ProjectPathUtil.TEMPLATE_DIR,
				templateName);
		if (wordprocessingMLPackage == null) {
			return;
		}

		TemplateWord templateWord = new TemplateWord(templateName);

		Map<String, TemplateContentControl> contentControlMap = new HashMap<>();

		templateWord.getSdtElementListMap().forEach((locationEnum, sdtElementListMap) -> {
			sdtElementListMap.forEach((tag, sdtElementList) -> {
				if (sdtElementList != null && !sdtElementList.isEmpty()) {
					TemplateContentControl contentControl = contentControlMap.get(tag);
					if (contentControl == null) {
						contentControl = new TemplateContentControl();
					}

					Set<TemplateContentControlLocationEnum> locationList = new HashSet<>();

					if (contentControl.getLocationList() != null) {
						locationList.addAll(contentControl.getLocationList());
					}

					contentControl.setLocationList(new ArrayList<>(locationList));

					contentControlMap.put(tag, contentControl);
				}
			});
		});

		if (contentControlMap.values() != null && !contentControlMap.values().isEmpty()) {
			TemplateContentControlUtil.processContentControlAndInsert(new ArrayList<>(contentControlMap.values()),
					templateWord, templateName);
		}

		if (docx4jProcessMethod != null) {
			wordprocessingMLPackage = docx4jProcessMethod.process(wordprocessingMLPackage);
		}

		if (wordprocessingMLPackage != null) {
			try {
				wordprocessingMLPackage.save(new File(ProjectPathUtil.TEMPLATE_OUTPUT_DIR, templateName));
			} catch (Docx4JException e) {
			}
		}
	}

	public static List<HeaderPart> retrieveAllHeaders(WordprocessingMLPackage wordprocessingMLPackage) {
		List<HeaderPart> headerParts = new ArrayList<>();
		if (wordprocessingMLPackage == null) {
			return headerParts;
		}

		for (SectionWrapper section : wordprocessingMLPackage.getDocumentModel().getSections()) {
			if (section.getHeaderFooterPolicy() == null) {
				continue;
			}

			if (section.getHeaderFooterPolicy().getDefaultHeader() != null) {
				headerParts.add(section.getHeaderFooterPolicy().getDefaultHeader());
			}
			if (section.getHeaderFooterPolicy().getEvenHeader() != null) {
				headerParts.add(section.getHeaderFooterPolicy().getEvenHeader());
			}
			if (section.getHeaderFooterPolicy().getFirstHeader() != null) {
				headerParts.add(section.getHeaderFooterPolicy().getFirstHeader());
			}
		}

		return headerParts;
	}

	public static List<FooterPart> retrieveAllFooters(WordprocessingMLPackage wordprocessingMLPackage) {
		List<FooterPart> footerParts = new ArrayList<>();
		if (wordprocessingMLPackage == null) {
			return footerParts;
		}

		for (SectionWrapper section : wordprocessingMLPackage.getDocumentModel().getSections()) {
			if (section.getHeaderFooterPolicy() == null) {
				continue;
			}

			if (section.getHeaderFooterPolicy().getDefaultFooter() != null) {
				footerParts.add(section.getHeaderFooterPolicy().getDefaultFooter());
			}
			if (section.getHeaderFooterPolicy().getEvenFooter() != null) {
				footerParts.add(section.getHeaderFooterPolicy().getEvenFooter());
			}
			if (section.getHeaderFooterPolicy().getFirstFooter() != null) {
				footerParts.add(section.getHeaderFooterPolicy().getFirstFooter());
			}
		}

		return footerParts;
	}

	public static Map<String, List<SdtElement>> retrieveAllSdtElements(List<? extends ContentAccessor> partList) {
		Map<String, List<SdtElement>> sdtElementListMap = new HashMap<>();
		if (partList == null) {
			return sdtElementListMap;
		}

		for (ContentAccessor part : partList) {
			for (Object content : part.getContent()) {
				if (content instanceof ContentAccessor) {
					List<Object> children = ((ContentAccessor) content).getContent();
					for (Object child : children) {
						if (child == null) {
							continue;
						}

						if (child instanceof ContentAccessor) {
							retrieveAllSdtElements(Arrays.asList((ContentAccessor) child)).forEach((key, val) -> {
								if (key != null && val != null && !val.isEmpty()) {
									List<SdtElement> sdtElementsList = sdtElementListMap.get(key);
									if (sdtElementsList == null) {
										sdtElementsList = new ArrayList<>();
									}
									sdtElementsList.addAll(val);
									sdtElementListMap.put(key, val);
								}
							});
							continue;
						}

						SdtElement sdtElement = null;
						String tag = null;
						if (child instanceof SdtBlock) {
							SdtBlock sdtBlock = (SdtBlock) child;
							SdtPr sdtProperties = sdtBlock.getSdtPr();
							if (sdtProperties != null) {
								tag = sdtProperties.getTag().getVal();
								sdtElement = sdtBlock;
							}
						} else if (child instanceof SdtRun) {
							SdtRun sdtRun = (SdtRun) child;
							SdtPr sdtProperties = sdtRun.getSdtPr();
							if (sdtProperties != null) {
								tag = sdtProperties.getTag().getVal();
								sdtElement = sdtRun;
							}
						}

						if (StringUtils.isBlank(tag) || sdtElement == null) {
							continue;
						}

						List<SdtElement> sdtElementsList = sdtElementListMap.get(tag);
						if (sdtElementsList == null) {
							sdtElementsList = new ArrayList<>();
						}

						sdtElementsList.add(sdtElement);
						sdtElementListMap.put(tag, sdtElementsList);
					}
				}
			}
		}
		return sdtElementListMap;
	}

	public static void insertImageForContentControl(final WordprocessingMLPackage wordMLPackage,
			final List<SdtElement> sdtElementList, final byte[] imageBytes, final int widthPixels,
			final int heightPixels, final JcEnumeration alignment, String name) {
		if (sdtElementList == null | sdtElementList.isEmpty()) {
			return;
		}

		final ObjectFactory factory = Context.getWmlObjectFactory();

		Inline inline = generateInline(wordMLPackage, null, imageBytes, widthPixels, heightPixels, name);

		for (final SdtElement sdtElement : sdtElementList) {
			if (sdtElement == null) {
				continue;
			}

			if (sdtElement instanceof SdtBlock) {
				SdtBlock block = (SdtBlock) sdtElement;
				List<Object> content = block.getSdtContent().getContent();
				content.clear();

				final P paragraph = addInlineImageToParagraph(inline);

				if (alignment != null) {
					paragraph.setPPr(factory.createPPr());
					paragraph.getPPr().setJc(factory.createJc());

					paragraph.getPPr().getJc().setVal(alignment);
				}

				content.add(paragraph);
				continue;
			}

			if (sdtElement instanceof SdtRun) {
				SdtRun sdtRun = (SdtRun) sdtElement;

				final List<Object> content = sdtRun.getSdtContent().getContent();
				content.clear();

				final RFonts rFont = new RFonts();
				rFont.setAscii("Arial");
				rFont.setHAnsi("Arial");
				rFont.setCs("Arial");

				final HpsMeasure hps = new HpsMeasure();
				hps.setVal(BigInteger.valueOf(20));

				RPr rPr = factory.createRPr();
				rPr.setRFonts(rFont);
				rPr.setSz(hps);
				rPr.setSzCs(hps);
				rPr.setB(new BooleanDefaultTrue());

				final R run = factory.createR();
				final Drawing drawing = factory.createDrawing();
				run.getContent().add(drawing);
				drawing.getAnchorOrInline().add(inline);

				content.add(run);

			}
		}
	}

	public static void insertContentControlTableInBody(final TemplateContentControlTable tableVO,
			final WordprocessingMLPackage wordMLPackage, final TemplateContentControlLocationEnum locationEnum) {

		List<? extends ContentAccessor> partList = locationEnum.equals(TemplateContentControlLocationEnum.BODY)
				? Arrays.asList(wordMLPackage.getMainDocumentPart())
				: locationEnum.equals(TemplateContentControlLocationEnum.HEADER) ? retrieveAllHeaders(wordMLPackage)
						: retrieveAllFooters(wordMLPackage);

		if (partList == null || partList.isEmpty()) {
			return;
		}

		List<Tbl> tableList = findTableListByContentControlTag(partList, tableVO.getTableTag());

		if (tableList == null || tableList.isEmpty()) {
			return;
		}

		for (Tbl table : tableList) {
			List<Object> headerObj = new ArrayList<>();
			List<Object> copiedRows = new ArrayList<>();

			if (table.getContent() != null && !table.getContent().isEmpty()) {
				int totalHeaderRows = tableVO.isFlagHasHeader()
						? tableVO.getNumHeaderRows() != null ? tableVO.getNumHeaderRows() : 1
						: 0;
				int totalCopiedRows = tableVO.isFlagCopyManyRows()
						? tableVO.getNumCopiedRows() != null ? tableVO.getNumCopiedRows() : 1
						: 0;

				for (int i = 0; i < table.getContent().size() && i < totalHeaderRows + totalCopiedRows; i++) {
					if (!(table.getContent().get(i) instanceof Tr)) {
						continue;
					}

					if (i < totalHeaderRows) {
						headerObj.add(table.getContent().get(i));
						continue;
					}

					if (i < totalCopiedRows + totalHeaderRows) {
						copiedRows.add(table.getContent().get(i));
					}
				}
			}

			// clear table and add header rows
			table.getContent().clear();
			if (headerObj != null && !headerObj.isEmpty()) {
				if (tableVO.getRows() != null) {
					for (Object obj : headerObj) {
						table.getContent().add(obj);
					}
				}
			}

			boolean flagFirstRow = true;
			for (Map<String, String> rowParams : tableVO.getRows()) {
				for (Object rowObj : copiedRows) {
					Tr tableRow = null;

					if (flagFirstRow) {
						tableRow = (Tr) rowObj;
					} else {
						tableRow = (Tr) XmlUtils.deepCopy(rowObj);
					}

					for (Object tr : tableRow.getContent()) {
						if (tr instanceof JAXBElement<?>) {
							Object rowValue = ((JAXBElement<?>) tr).getValue();
							if (rowValue instanceof Tc) {
								Tc cell = (Tc) rowValue;
								Map<String, List<SdtElement>> sdtElementListMap = retrieveAllSdtElements(
										Arrays.asList(cell));
								for (Entry<String, String> entry : rowParams.entrySet()) {
									insertValueForContentControl(cell, entry.getValue(),
											sdtElementListMap.get(entry.getKey()));
								}
							}
						}
					}

					table.getContent().add(tableRow);
				}
				if (flagFirstRow) {
					flagFirstRow = false;
				}
			}

			if (!tableVO.isFlagExternalResource()) {
				continue;
			}
		}
	}

	private static List<Tbl> findTableListByContentControlTag(List<? extends ContentAccessor> partList, String tag) {
		List<Tbl> tableList = new ArrayList<>();
		for (ContentAccessor part : partList) {
			for (Object obj : part.getContent()) {
				if (obj instanceof SdtElement) {
					SdtElement contentControl = (SdtElement) obj;
					SdtPr sdtPr = contentControl.getSdtPr();
					if (sdtPr != null && sdtPr.getTag() != null && tag.equals(sdtPr.getTag().getVal())) {
						for (Object sdtContentObj : contentControl.getSdtContent().getContent()) {
							if (sdtContentObj instanceof JAXBElement) {
								Object sdtContent = ((JAXBElement<?>) sdtContentObj).getValue();
								if (sdtContent instanceof Tbl) {
									tableList.add((Tbl) sdtContent);
								}
							} else if (sdtContentObj instanceof Tbl) {
								tableList.add((Tbl) sdtContentObj);
							}
						}
					}
				}
			}
		}
		return tableList;
	}

	private static P addInlineImageToParagraph(final Inline inline) {
		// Now add the in-line image to a paragraph
		final ObjectFactory factory = new ObjectFactory();
		final P paragraph = factory.createP();

		paragraph.getContent().clear();

		PPr pPr = factory.createPPr();
		RPr rPr = factory.createRPr();
		paragraph.getContent().add(pPr);
		ParaRPr paraRPr = factory.createParaRPr();
		final RFonts rFont = new RFonts();
		final HpsMeasure hps = new HpsMeasure();
		hps.setVal(BigInteger.valueOf(20));
		rFont.setAscii("Arial");
		rFont.setHAnsi("Arial");
		rFont.setCs("Arial");
		rPr.setRFonts(rFont);
		rPr.setSz(hps);
		rPr.setSzCs(hps);
		rPr.setB(new BooleanDefaultTrue());
		paraRPr.setRFonts(rFont);
		paraRPr.setSz(hps);
		paraRPr.setSzCs(hps);

		Spacing spacing = factory.createPPrBaseSpacing();
		pPr.setSpacing(spacing);
		spacing.setAfter(BigInteger.valueOf(0));
		Jc jc = factory.createJc();
		jc.setVal(JcEnumeration.BOTH);
		pPr.setJc(jc);
		pPr.setRPr(paraRPr);

		final R run = factory.createR();
		paragraph.getContent().add(run);
		final Drawing drawing = factory.createDrawing();
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);
		return paragraph;
	}

	private static Inline generateInline(WordprocessingMLPackage wordMLPackage, Part sourcePart, byte[] imageBytes,
			final int widthPixels, final int heightPixels, String name) {
		BinaryPartAbstractImage imagePart = null;

		if (sourcePart == null) {
			try {
				imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imageBytes);
			} catch (final Exception e) {
			}
		} else {
			try {
				imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, sourcePart, imageBytes);
			} catch (final Exception e) {
			}
		}

		final int docPrId = 1;
		final int cNvPrId = 2;

		// conversion from pixels to EMU
		final long width = widthPixels * 9525L;
		final long height = heightPixels * 9525L;

		Inline inline = null;
		try {
			inline = imagePart.createImageInline(name, name, docPrId, cNvPrId, width, height, false);
		} catch (final Exception e) {
		}

		return inline;
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

	public static void insertValueForContentControl(final Object part, final String value,
			final List<SdtElement> sdtElementList) {
		if (part == null || sdtElementList == null || sdtElementList.isEmpty()) {
			return;
		}

		final ObjectFactory factory = Context.getWmlObjectFactory();
		final String[] split = StringUtils.isBlank(value) ? new String[] { StringUtils.EMPTY } : value.split("\n");

		for (SdtElement sdtElement : sdtElementList) {
			if (sdtElement == null) {
				continue;
			}

			if (sdtElement instanceof SdtBlock) {
				SdtBlock block = (SdtBlock) sdtElement;
				ContentAccessor blockContent = block.getSdtContent();
				RPr rPr = getSdtBlockRPr(block, factory);

				blockContent.getContent().clear();

				for (final String string : split) {
					final Text text = factory.createText();
					text.setSpace(PRESERVE);
					text.setValue(string);

					final R run = factory.createR();
					run.getContent().add(text);
					run.setRPr(rPr);

					final P para = factory.createP();
					para.getContent().add(run);

					PPr pPr = factory.createPPr();
					ParaRPr paraRPr = factory.createParaRPr();
					paraRPr.setRFonts(rPr.getRFonts());

					paraRPr.setRFonts(rPr.getRFonts());
					paraRPr.setSz(rPr.getSz());
					paraRPr.setSzCs(rPr.getSzCs());

					Spacing spacing = factory.createPPrBaseSpacing();
					pPr.setSpacing(spacing);
					spacing.setAfter(BigInteger.valueOf(0));
					pPr.setRPr(paraRPr);

					blockContent.getContent().add(para);
				}
				continue;
			}

			if (sdtElement instanceof SdtRun) {
				SdtRun sdtRun = (SdtRun) sdtElement;

				final R run = factory.createR();
				final RPr rpr = getSdtRunRPr(sdtRun, factory);

				List<Object> runContent = run.getContent();

				for (int i = 0; i < split.length; i++) {
					String string = split[i];
					final Text text = factory.createText();
					text.setValue(string);
					text.setSpace(PRESERVE);
					runContent.add(text);

					// add line break if its not the last
					if (i != split.length - 1) {
						Br br = factory.createBr();
						runContent.add(br);
					}
				}

				run.setRPr(rpr);

				List<Object> content = sdtRun.getSdtContent().getContent();
				content.clear();
				content.add(run);
			}
		}
	}

	public static RPr getSdtBlockRPr(final SdtBlock sdtBlock, final ObjectFactory factory) {
		if (sdtBlock == null || factory == null) {
			return null;
		}

		RPr rPr = factory.createRPr();
		for (Object content : sdtBlock.getSdtContent().getContent()) {
			if (content instanceof P) {
				P p = (P) content;
				List<Object> pContent = p.getContent();
				for (Object runObj : pContent) {
					if (runObj instanceof R) {
						R run = (R) runObj;
						RPr rPrC = run.getRPr();
						if (rPrC != null) {
							rPr = rPrC;
							break;
						}
					}
				}
			}
		}

		return rPr;
	}

	public static RPr getSdtRunRPr(final SdtRun sdtRun, final ObjectFactory factory) {
		if (sdtRun == null || factory == null) {
			return null;
		}

		RPr rPr = factory.createRPr();
		for (Object content : sdtRun.getSdtContent().getContent()) {
			if (content instanceof R) {
				R r = (R) content;
				RPr rPrC = r.getRPr();
				if (rPrC != null) {
					rPr = rPrC;
					break;
				}
			}
		}

		return rPr;
	}
}

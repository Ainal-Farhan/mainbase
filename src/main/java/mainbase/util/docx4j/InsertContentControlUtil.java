package mainbase.util.docx4j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTSdtCell;
import org.docx4j.wml.CTSdtRow;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.SdtBlock;
import org.docx4j.wml.SdtElement;
import org.docx4j.wml.SdtRun;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.PPrBase.Spacing;

import jakarta.xml.bind.JAXBElement;
import mainbase.enums.TemplateContentControlLocationEnum;
import mainbase.template.TemplateContentControlTable;

public class InsertContentControlUtil {
    private static final String PRESERVE = "preserve";

    public static void insertImageForContentControlIntoTemplate(final WordprocessingMLPackage wordMLPackage,
            final List<? extends ContentAccessor> contentAccessorList, final byte[] imageBytes, final int widthPixels,
            final int heightPixels, final JcEnumeration alignment, String name) {
        if (contentAccessorList == null | contentAccessorList.isEmpty()) {
            return;
        }

        final List<SdtElement> sdtElementList = TemplateUtil.retrieveAllSdtElements(contentAccessorList).get(name);
        if (sdtElementList == null || sdtElementList.isEmpty()) {
            return;
        }

        final ObjectFactory factory = Context.getWmlObjectFactory();
        Inline inline = generateInline(wordMLPackage,
                contentAccessorList.get(0) instanceof MainDocumentPart ? null : (Part) contentAccessorList.get(0),
                imageBytes, widthPixels, heightPixels, name);

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

    public static void insertContentControlTableIntoTemplate(final TemplateContentControlTable tableVO,
            final WordprocessingMLPackage wordMLPackage, final TemplateContentControlLocationEnum locationEnum) {

        List<? extends ContentAccessor> partList = locationEnum.equals(TemplateContentControlLocationEnum.BODY)
                ? Arrays.asList(wordMLPackage.getMainDocumentPart())
                : TemplateUtil.retrieveAllFootersSeperately(wordMLPackage, locationEnum);

        if (partList == null || partList.isEmpty()) {
            partList = TemplateUtil.retrieveAllHeadersSeperately(wordMLPackage, locationEnum);
        }

        if (partList == null || partList.isEmpty()) {
            return;
        }

        List<Tbl> tableList = TemplateUtil.retrieveAllTbls(partList).get(tableVO.getTableTag());

        if (tableList == null || tableList.isEmpty()) {
            List<SdtElement> sdtElements = TemplateUtil.retrieveAllSdtElements(partList).get(tableVO.getTableTag());
            if (sdtElements != null && !sdtElements.isEmpty()) {
                for (SdtElement sdtElement : sdtElements) {
                    Tbl tbl = TemplateUtil.findNearestTable(sdtElement);
                    if (tbl != null) {
                        if (tableList == null) {
                            tableList = new ArrayList<>();
                        }
                        tableList.add(tbl);
                    }
                }
            }
        }

        if (tableList == null || tableList.isEmpty()) {
            return;
        }

        for (Tbl table : tableList) {
            if (table.getContent() == null || table.getContent().isEmpty()) {
                continue;
            }

            List<Object> headerList = new ArrayList<>();
            List<Object> copiedRows = new ArrayList<>();

            int totalHeaderRows = tableVO.isFlagHasHeader()
                    ? tableVO.getNumHeaderRows() != null ? tableVO.getNumHeaderRows() : 1
                    : 0;
            int totalCopiedRows = tableVO.isFlagCopyManyRows() && tableVO.getNumCopiedRows() != null
                    ? tableVO.getNumCopiedRows()
                    : 1;

            collectHeaderAndCopiedRows(table, headerList, copiedRows, totalHeaderRows, totalCopiedRows);

            // Clear the table and add header rows
            table.getContent().clear();
            table.getContent().addAll(headerList);

            for (Map<String, String> rowParams : tableVO.getRows()) {
                addCopiedRowsToTable(table, copiedRows, rowParams);
            }
        }
    }

    private static void collectHeaderAndCopiedRows(Tbl table, List<Object> headerList, List<Object> copiedRows,
            int totalHeaderRows, int totalCopiedRows) {
        for (int i = 0; i < table.getContent().size() && i < totalHeaderRows + totalCopiedRows; i++) {
            Object content = table.getContent().get(i);

            if (content instanceof CTSdtRow
                    || (content instanceof JAXBElement<?>
                            && ((JAXBElement<?>) content).getDeclaredType() == CTSdtRow.class)
                    || (content instanceof Tr || (content instanceof JAXBElement<?>
                            && ((JAXBElement<?>) content).getDeclaredType() == Tr.class))) {
                if (i < totalHeaderRows) {
                    headerList.add(content instanceof JAXBElement<?> ? ((JAXBElement<?>) content).getValue() : content);
                } else if (i < totalCopiedRows + totalHeaderRows) {
                    copiedRows.add(content instanceof JAXBElement<?> ? ((JAXBElement<?>) content).getValue() : content);
                }
            }
        }
    }

    public static void insertValueForContentControlIntoTemplate(List<? extends ContentAccessor> contentAccessorList,
            String tag, String value) {
        if (contentAccessorList == null || contentAccessorList.isEmpty() || StringUtils.isBlank(tag)
                || StringUtils.isBlank(value)) {
            return;
        }

        final List<SdtElement> sdtElementList = TemplateUtil.retrieveAllSdtElements(contentAccessorList).get(tag);
        if (sdtElementList == null || sdtElementList.isEmpty()) {
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

    private static void addCopiedRowsToTable(Tbl table, List<Object> copiedRows, Map<String, String> rowParams) {
        for (Object rowObj : copiedRows) {
            List<Object> contentList = new ArrayList<>();
            if (rowObj instanceof Tr) {
                Tr tableRow = (Tr) XmlUtils.deepCopy(rowObj);
                if (tableRow.getContent() != null) {
                    contentList = tableRow.getContent();
                }

                table.getContent().add(tableRow);
            } else if (rowObj instanceof CTSdtRow) {
                CTSdtRow copiedSdtRow = (CTSdtRow) XmlUtils.deepCopy(rowObj);
                if (copiedSdtRow.getSdtContent() != null && copiedSdtRow.getSdtContent().getContent() != null) {
                    contentList.clear();
                    for (Object content : copiedSdtRow.getSdtContent().getContent()) {
                        if (content instanceof JAXBElement<?>) {
                            content = ((JAXBElement<?>) content).getValue();
                        }
                        if (content != null && content instanceof Tr) {
                            Tr tableRow = (Tr) XmlUtils.deepCopy(content);
                            if (tableRow.getContent() != null) {

                                for (Object tr : tableRow.getContent()) {
                                    if (tr instanceof JAXBElement<?>) {
                                        tr = ((JAXBElement<?>) tr).getValue();
                                    }
                                    if (tr instanceof CTSdtCell) {
                                        CTSdtCell sdtCell = (CTSdtCell) tr;
                                        if (sdtCell.getSdtContent() != null
                                                && sdtCell.getSdtContent().getContent() != null) {
                                            for (Object c : sdtCell.getSdtContent().getContent()) {

                                                if (c instanceof JAXBElement<?>) {
                                                    c = ((JAXBElement<?>) c).getValue();
                                                }

                                                if (c instanceof Tc) {
                                                    contentList.add(c);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (Object tr : contentList) {
                Object rowValue = tr;
                if (tr instanceof JAXBElement<?>) {
                    rowValue = ((JAXBElement<?>) tr).getValue();
                }
                if (rowValue != null && rowValue instanceof Tc) {
                    Tc cell = (Tc) rowValue;
                    for (Entry<String, String> entry : rowParams.entrySet()) {
                        insertValueForContentControlIntoTemplate(Arrays.asList(cell), entry.getKey(), entry.getValue());
                    }
                }
            }
        }
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

    private static RPr getSdtRunRPr(final SdtRun sdtRun, final ObjectFactory factory) {
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

    private static RPr getSdtBlockRPr(final SdtBlock sdtBlock, final ObjectFactory factory) {
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
}

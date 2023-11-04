package mainbase.util.docx4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;

import mainbase.enums.TemplateContentControlLocationEnum;
import mainbase.enums.TemplateContentControlTypeEnum;
import mainbase.functional.TemplateContentControlMethod;
import mainbase.functional.parameter.TemplateContentControlMethodParameter;
import mainbase.template.TemplateContentControlTable;
import mainbase.util.file.FileUtil;
import mainbase.util.path.ProjectPathUtil;

public class TemplateContentControlMethodUtil {
    public static WordprocessingMLPackage processExample1(WordprocessingMLPackage wordprocessingMLPackage) {
        MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();

        ObjectFactory factory = new ObjectFactory();

        P paragraph = factory.createP();
        R run = factory.createR();
        Text text = factory.createText();

        text.setValue("Adding new Paragraph");
        run.getContent().add(text);
        paragraph.getContent().add(run);
        mainDocumentPart.getContent().add(paragraph);

        return wordprocessingMLPackage;

    };

    public static final TemplateContentControlMethod processExample1ExternalTableCC = (
            final TemplateContentControlMethodParameter parameter) -> {
        parameter.contentControl.setType(TemplateContentControlTypeEnum.TABLE);

        TemplateContentControlTable tableCC = parameter.contentControl
                .retrieveValueAs(TemplateContentControlTable.class);
        if (tableCC == null) {
            tableCC = new TemplateContentControlTable(parameter.contentControl.getTag());
        }

        tableCC.setFlagHasHeader(true);

        tableCC.setFlagExternalResource(true);
        tableCC.setExternalLocationTable(TemplateContentControlLocationEnum.BODY);
        tableCC.setExternalPath(ProjectPathUtil.TEMPLATE_REF_DIR);
        tableCC.setExternalFilename("ExampleReferenceTable1.docx");
        tableCC.setExternalTableTag("externalTable1");

        Map<String, String> eachRowVal = new HashMap<>();
        eachRowVal.put("row1col1", "Test for col 1");
        eachRowVal.put("row1col2", "Test for col 2");
        eachRowVal.put("row1col3", "Test for col 3");
        tableCC.getRows().add(eachRowVal);

        parameter.contentControl.setValue(tableCC);
    };

    public static final TemplateContentControlMethod processExample1TableCC = (
            final TemplateContentControlMethodParameter parameter) -> {
        parameter.contentControl.setType(TemplateContentControlTypeEnum.TABLE);

        TemplateContentControlTable tableCC = parameter.contentControl
                .retrieveValueAs(TemplateContentControlTable.class);
        if (tableCC == null) {
            tableCC = new TemplateContentControlTable(parameter.contentControl.getTag());
        }

        tableCC.setFlagHasHeader(true);

        Map<String, String> eachRowVal = new HashMap<>();
        eachRowVal.put("row1column1", "Test for col 1");
        eachRowVal.put("row1column2", "Test for col 2");
        tableCC.getRows().add(eachRowVal);

        parameter.contentControl.setValue(tableCC);
    };

    public static final TemplateContentControlMethod processExample2TableCC = (
            final TemplateContentControlMethodParameter parameter) -> {
        parameter.contentControl.setType(TemplateContentControlTypeEnum.TABLE);

        TemplateContentControlTable tableCC = parameter.contentControl
                .retrieveValueAs(TemplateContentControlTable.class);
        if (tableCC == null) {
            tableCC = new TemplateContentControlTable(parameter.contentControl.getTag());
        }

        Map<String, String> eachRowVal = new HashMap<>();
        eachRowVal.put("row1column1", "Test for col 1");
        eachRowVal.put("row1column2", "Test for col 2");
        tableCC.getRows().add(eachRowVal);

        parameter.contentControl.setValue(tableCC);
    };

    public static final TemplateContentControlMethod processExample3CC = (
            final TemplateContentControlMethodParameter parameter) -> {
        parameter.contentControl.setType(TemplateContentControlTypeEnum.TEXT);
        parameter.contentControl.setValue("Test la weyh");
    };

    public static final TemplateContentControlMethod processExample4ImageCC = (
            final TemplateContentControlMethodParameter parameter) -> {
        parameter.contentControl.setType(TemplateContentControlTypeEnum.IMAGE);
        File imageFile = new File(ProjectPathUtil.RESOURCE_DIR, "test.png");
        try {
            parameter.contentControl.setValue(FileUtil.fileToByteArray(imageFile));
        } catch (IOException e) {
        }
    };

    public static final TemplateContentControlMethod processExample5CC = (
            final TemplateContentControlMethodParameter parameter) -> {
        parameter.contentControl.setType(TemplateContentControlTypeEnum.TEXT);
        parameter.contentControl.setValue("Test la weyh 2");
    };
}

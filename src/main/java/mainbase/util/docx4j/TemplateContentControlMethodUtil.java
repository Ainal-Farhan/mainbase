package mainbase.util.docx4j;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;

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
}

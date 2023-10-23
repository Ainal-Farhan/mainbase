package mainbase.main;

import java.util.Arrays;

import mainbase.builder.viewobject.ExampleVOBuilder;
import mainbase.constant.TemplateConstant;
import mainbase.domain.Example2Domain;
import mainbase.domain.ExampleDomain;
import mainbase.util.docx4j.TemplateUtil;
import mainbase.viewobject.ExampleVO;

public class Main {

	public static void main(String[] args) {
		System.out.println("Start Example Builder:");
		exampleBuilder();
		System.out.println("---------------------\n");

		System.out.println("Start Example Docx Template:");
		exampleDocx4j();
		System.out.println("----------------------------\n");

		System.out.println("Start Example Docx Template No 2 (Multiple Template Concurrently):");
		exampleDocx4jNo2();
		System.out.println("----------------------------\n");
	}

	private static void exampleBuilder() {
		ExampleDomain exampleDomain = new ExampleDomain();
		exampleDomain.setTitle("Example");
		exampleDomain.setDescription("Description of this example is ...");

		Example2Domain example2Domain = new Example2Domain();
		example2Domain.setWhatToDo("I can do anything!");

		ExampleVOBuilder exampleBuilder = new ExampleVOBuilder();
		ExampleVO vo = exampleBuilder.buildFromExample(exampleDomain).buildFromExample2(example2Domain).build();

		System.out.println("Result:");
		System.out.println("title: " + vo.getTitle());
		System.out.println("desc: " + vo.getDescription());
		System.out.println("what to do: " + vo.getWhatToDo());

		System.out.println("\n----");
		System.out.println("Retrieve exampleDomain:");
		ExampleDomain example1 = ExampleVOBuilder.destructIntoExample(null, vo);
		System.out.println("title: " + example1.getTitle());
		System.out.println("desc: " + example1.getDescription());

		System.out.println("\n----");
		System.out.println("Retrieve exampleDomain2:");
		Example2Domain example2 = ExampleVOBuilder.destructIntoExample2(null, vo);
		System.out.println("what to do: " + example2.getWhatToDo());

	}

	private static void exampleDocx4j() {
		TemplateUtil.processTemplate(TemplateConstant.TEMPLATE_EXAMPLE_1);
	}

	private static void exampleDocx4jNo2() {
		TemplateUtil.processMultipleTemplateConcurrently(Arrays.asList("Test-Template2.docx", "Test-Template3.docx",
				"Test-Template4.docx", "Test-Template5.docx", "Test-Template6.docx", "Test-Template7.docx",
				"Test-Template8.docx", "Test-Template9.docx"));
	}

}

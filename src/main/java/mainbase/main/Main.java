package mainbase.main;

import java.util.Arrays;

import mainbase.builder.viewobject.ExampleVOBuilder;
import mainbase.constant.TemplateConstant;
import mainbase.domain.Example2Domain;
import mainbase.domain.ExampleDomain;
import mainbase.factory.ViewObjectFactory;
import mainbase.functional.parameter.ViewObjectFactoryMethodParameter;
import mainbase.util.docx4j.TemplateUtil;
import mainbase.viewobject.ExampleVO;

public class Main {

    public static void main(String[] args) {
        int example = 2;

        switch (example) {
        case 1:
            exampleBuilder();
            break;
        case 2:
            exampleDocx4j();
            break;
        case 3:
            exampleDocx4jConcurrent();
            break;
        case 4:
            exampleStaticFactory();
            break;
        default:
            exampleBuilder();
            exampleDocx4j();
            exampleDocx4jConcurrent();
            exampleStaticFactory();
        }
    }

    private static void exampleStaticFactory() {
        System.out.println("Start Example Static Factory:");

        ExampleDomain exampleDomain = new ExampleDomain();
        exampleDomain.setTitle("Example");
        exampleDomain.setDescription("Description of this example is ...");

        Example2Domain example2Domain = new Example2Domain();
        example2Domain.setWhatToDo("I can do anything!");

        ViewObjectFactoryMethodParameter parameter = new ViewObjectFactoryMethodParameter.Builder()
                .withExampleDomain(exampleDomain).withExample2Domain(example2Domain).build();

        ExampleVO vo = ViewObjectFactory.create(ExampleVO.class, parameter);

        System.out.println("title: " + vo.getTitle());
        System.out.println("desc: " + vo.getDescription());
        System.out.println("what to do: " + vo.getWhatToDo());

        System.out.println("----------------------------\n");
    }

    private static void exampleBuilder() {
        System.out.println("Start Example Builder:");

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

        System.out.println("----------------------------\n");
    }

    private static void exampleDocx4j() {
        System.out.println("Start Example Docx Template:");

        TemplateUtil.processTemplate(TemplateConstant.KEY_TEMPLATE_EXAMPLE_TABLE_CC);

        System.out.println("----------------------------\n");
    }

    private static void exampleDocx4jConcurrent() {
        System.out.println("Start Example Docx Template No 2 (Multiple Template Concurrently):");

        TemplateUtil.processMultipleTemplateConcurrently(Arrays.asList(TemplateConstant.KEY_TEMPLATE_EXAMPLE_1,
                "Test-Template3.docx", "Test-Template4.docx", "Test-Template5.docx", "Test-Template6.docx",
                "Test-Template7.docx", "Test-Template8.docx", "Test-Template9.docx"), 4);

        System.out.println("----------------------------\n");
    }

}

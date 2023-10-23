package mainbase.builder.viewobject;

import mainbase.domain.Example2Domain;
import mainbase.domain.ExampleDomain;
import mainbase.viewobject.ExampleVO;

public class ExampleVOBuilder implements IViewObjectBuilder<ExampleVO> {

	private static final long serialVersionUID = 1L;

	private ExampleVO vo = new ExampleVO();

	public ExampleVOBuilder buildFromExample(ExampleDomain example) {
		if (example != null) {
			vo.setTitle(example.getTitle());
			vo.setDescription(example.getDescription());
		}
		return this;
	}

	public static ExampleDomain destructIntoExample(ExampleDomain example, ExampleVO vo) {
		if (example == null) {
			example = new ExampleDomain();
		}

		example.setTitle(vo.getTitle());
		example.setDescription(vo.getDescription());

		return example;
	}

	public ExampleVOBuilder buildFromExample2(Example2Domain example2) {
		if (example2 != null) {
			vo.setWhatToDo(example2.getWhatToDo());
		}

		return this;
	}

	public static Example2Domain destructIntoExample2(Example2Domain example2, ExampleVO vo) {
		if (example2 == null) {
			example2 = new Example2Domain();
		}

		example2.setWhatToDo(vo.getWhatToDo());

		return example2;
	}

	public ExampleVO build() {
		return vo;
	}

}

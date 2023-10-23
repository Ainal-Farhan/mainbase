package mainbase.viewobject;

public class ExampleVO implements IViewObject {

	private static final long serialVersionUID = 1L;

	private String title;

	private String description;

	private String whatToDo;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWhatToDo() {
		return whatToDo;
	}

	public void setWhatToDo(String whatToDo) {
		this.whatToDo = whatToDo;
	}

}

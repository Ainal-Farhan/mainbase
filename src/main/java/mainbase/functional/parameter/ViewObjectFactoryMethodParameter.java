package mainbase.functional.parameter;

import mainbase.builder.IBuilder;
import mainbase.domain.Example2Domain;
import mainbase.domain.ExampleDomain;

public class ViewObjectFactoryMethodParameter {
    private String title;
    private String description;
    private String whatToDo;

    protected ViewObjectFactoryMethodParameter(ExampleDomain exampleDomain, Example2Domain example2Domain) {
        if (exampleDomain != null) {
            title = exampleDomain.getTitle();
            description = exampleDomain.getDescription();
        }

        if (example2Domain != null) {
            whatToDo = example2Domain.getWhatToDo();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getWhatToDo() {
        return whatToDo;
    }

    public static class Builder implements IBuilder<ViewObjectFactoryMethodParameter> {
        private static final long serialVersionUID = 1L;

        private ExampleDomain exampleDomain;
        private Example2Domain example2Domain;

        public Builder withExampleDomain(ExampleDomain exampleDomain) {
            this.exampleDomain = exampleDomain;
            return this;
        }

        public Builder withExample2Domain(Example2Domain example2Domain) {
            this.example2Domain = example2Domain;
            return this;
        }

        @Override
        public ViewObjectFactoryMethodParameter build() {
            return new ViewObjectFactoryMethodParameter(exampleDomain, example2Domain);
        }

    }

}

package mainbase.vo;

import java.io.Serializable;

public abstract class BaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private VOParameter parameter;

    protected BaseVO(VOParameter parameter) {
        this.parameter = parameter;
    }

    public abstract void init();

    public VOParameter getParameter() {
        return parameter;
    }

    public void setParameter(VOParameter parameter) {
        this.parameter = parameter;
    }

}

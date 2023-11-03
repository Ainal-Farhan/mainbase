package mainbase.config;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Config implements Serializable {

    private static final long serialVersionUID = 1L;
    protected static Logger log = LoggerFactory.getLogger(Config.class);

    protected abstract void init();
}

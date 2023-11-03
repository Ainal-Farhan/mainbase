package mainbase.functional;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;

@FunctionalInterface
public interface JsonFileProcessMethod {
    void process(JsonParser jsonParser, Object[] params) throws IOException;
}

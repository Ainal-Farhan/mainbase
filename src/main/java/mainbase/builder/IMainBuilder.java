package mainbase.builder;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

public interface IMainBuilder<T, K> extends Serializable {
    T build(K parameter, Class<? extends T> a) throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}

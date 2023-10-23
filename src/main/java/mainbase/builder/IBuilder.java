package mainbase.builder;

import java.io.Serializable;

public interface IBuilder<T> extends Serializable {
	T build();
}

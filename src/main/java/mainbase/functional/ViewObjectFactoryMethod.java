package mainbase.functional;

import mainbase.functional.parameter.ViewObjectFactoryMethodParameter;
import mainbase.viewobject.IViewObject;

@FunctionalInterface
public interface ViewObjectFactoryMethod<T extends IViewObject> {
    T create(ViewObjectFactoryMethodParameter parameter);
}

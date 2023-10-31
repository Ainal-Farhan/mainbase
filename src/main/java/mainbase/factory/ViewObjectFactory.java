package mainbase.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mainbase.functional.ViewObjectFactoryMethod;
import mainbase.functional.parameter.ViewObjectFactoryMethodParameter;
import mainbase.viewobject.ExampleVO;
import mainbase.viewobject.IViewObject;

public class ViewObjectFactory {
    private static Map<Class<? extends IViewObject>, ViewObjectFactoryMethod<? extends IViewObject>> CREATION_METHOD_MAP;

    public static <T extends IViewObject> T create(Class<T> clazz, ViewObjectFactoryMethodParameter parameter) {
        if (!IViewObject.class.isAssignableFrom(clazz)) {
            return null;
        }
        ViewObjectFactoryMethod<? extends IViewObject> createMethod = CREATION_METHOD_MAP.get(clazz);
        if (createMethod != null) {
            var product = createMethod.create(parameter);
            if (clazz.isInstance(product)) {
                return clazz.cast(product);
            }
        }
        return null;
    }

    private static ViewObjectFactoryMethod<ExampleVO> createExampleVO = (parameter) -> {
        ExampleVO vo = new ExampleVO();

        vo.setTitle(parameter.getTitle());
        vo.setDescription(parameter.getDescription());
        vo.setWhatToDo(parameter.getWhatToDo());

        return vo;
    };

    static {
        Map<Class<? extends IViewObject>, ViewObjectFactoryMethod<? extends IViewObject>> creationMethodMap = new HashMap<>();
        creationMethodMap.put(ExampleVO.class, createExampleVO);
        CREATION_METHOD_MAP = Collections.unmodifiableMap(creationMethodMap);
    }
}

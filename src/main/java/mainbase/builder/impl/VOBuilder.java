package mainbase.builder.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import mainbase.builder.IMainBuilder;
import mainbase.vo.BaseVO;
import mainbase.vo.VOParameter;

public class VOBuilder implements IMainBuilder<BaseVO, VOParameter> {

    private static final long serialVersionUID = 1L;

    public BaseVO build(VOParameter parameter, Class<? extends BaseVO> clazz)
            throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Class<?>[] parameterTypes = { parameter.getClass() };

        Constructor<? extends BaseVO> constructor = clazz.getDeclaredConstructor(parameterTypes);

        BaseVO vo = constructor.newInstance(parameter);
        vo.init();

        return vo;
    }

}

package mainbase.util.docx4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.docx4j.TraversalUtil;
import org.docx4j.wml.SdtElement;
import org.docx4j.wml.SdtPr;
import org.docx4j.wml.Tbl;

public class TblFinder extends TraversalUtil.CallbackImpl {
    private Map<String, List<Tbl>> tblsByTag = new HashMap<>();

    @Override
    public List<Object> apply(Object o) {
        if (o instanceof Tbl) {
            Tbl tbl = (Tbl) o;
            findSdtElementAndAdd(tbl, tbl);
        }
        return null;
    }

    public Map<String, List<Tbl>> getTblsByTag() {
        return tblsByTag;
    }

    private void findSdtElementAndAdd(Object obj, Tbl tbl) {
        Method getParentMethod = null;
        Object parent = null;
        try {
            getParentMethod = obj != null ? obj.getClass().getMethod("getParent") : null;
            if (getParentMethod == null) {
                return;
            }
            parent = getParentMethod.invoke(obj);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
        }

        if (parent == null) {
            return;
        }
        if (parent instanceof SdtElement) {
            SdtPr sdtPr = ((SdtElement) parent).getSdtPr();
            if (sdtPr != null && sdtPr.getTag() != null) {
                String tagValue = sdtPr.getTag().getVal();
                tblsByTag.computeIfAbsent(tagValue, k -> new ArrayList<>()).add(tbl);
                return;
            }
        }

        findSdtElementAndAdd(parent, tbl);
    }
}

package mainbase.util.docx4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.docx4j.TraversalUtil;
import org.docx4j.wml.SdtElement;
import org.docx4j.wml.SdtPr;
import org.docx4j.wml.Tag;

public class SdtElementFinder extends TraversalUtil.CallbackImpl {
    private Map<String, List<SdtElement>> sdtElementsByTag = new HashMap<>();

    @Override
    public List<Object> apply(Object o) {
        if (o instanceof SdtElement) {
            SdtElement sdtElement = (SdtElement) o;

            // Check for the presence of a tag and categorize by tag value
            SdtPr sdtPr = sdtElement.getSdtPr();
            if (sdtPr != null) {
                Tag tag = sdtPr.getTag();
                if (tag != null && tag.getVal() != null) {
                    String tagValue = tag.getVal();
                    sdtElementsByTag.computeIfAbsent(tagValue, k -> new ArrayList<>()).add(sdtElement);
                }
            }
        }
        return null;
    }

    public Map<String, List<SdtElement>> getSdtElementsByTag() {
        return sdtElementsByTag;
    }
}
package gov.scot.www;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

public class FolderUtils {

    private FolderUtils() {
        // util class
    }

    public static boolean hasFolderType(Node node, String type) throws RepositoryException {
        if (!node.isNodeType("hippostd:folder")) {
            return false;
        }

        // if a month folder is created then alter the foldertype if if is either minutes or speech / statement
        Property prop = node.getProperty("hippostd:foldertype");
        Value[] values = prop.getValues();
        for (Value v : values) {
            String val = v.getString();
            if (val.equals(type)) {
                return true;
            }
        }
        return false;
    }
}

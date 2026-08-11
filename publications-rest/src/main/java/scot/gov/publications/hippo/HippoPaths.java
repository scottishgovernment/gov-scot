package scot.gov.publications.hippo;

import com.github.slugify.Slugify;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import java.util.*;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Creates nodes in the repository for given hippoPaths.
 */
public class HippoPaths {

    private static List<String> stopWords = new ArrayList<>();

    public static final String ROOT = "/content/documents/govscot/";

    public static final String IMG_ROOT = "/content/gallery/";

    private static final String[] EMPTY_STRINGS = new String[0];

    Session session;

    private static final Slugify slugify = new Slugify();

    public HippoPaths(Session session) {
        this.session = session;
        Map<String, String> replacements = new HashMap<>();
        replacements.put("+", "");
        replacements.put("'", "");
        replacements.put("&", "");
        slugify.withCustomReplacements(replacements);
    }

    static {
        initStopwords();
    }

    private static void initStopwords() {
        String stopwords =
                "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been," +
                "but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got," +
                "had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its," +
                "just,least,let,like,likely,may,me,might,most,must,my,neither," +
                "of,off,often,on,or,other,our,own,rather,said,say,says,she,should,since,so,some," +
                "than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us," +
                "wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your";
        stopWords.addAll(asList(stopwords.split(",")));
    }

    public static String slugify(String pathElement, boolean removeStopwords) {
        return removeStopwords
                ? slugify(pathElement)
                : slugify.slugify(pathElement);
    }

    public static String slugify(String pathElement) {
        String slug = slugify.slugify(pathElement);

        StringBuilder simplifiedSlug = new StringBuilder();
        for(String word : slug.split("-")) {
            if(isNotEmpty(word) && !stopWords.contains(word)) {
                simplifiedSlug.append(word).append('-');
            }
        }
        return simplifiedSlug.deleteCharAt(simplifiedSlug.length() - 1).toString();
    }

    public Node ensurePath(List<String> path) throws RepositoryException {
        return ensurePath(ROOT, path);
    }

    public Node ensurePath(String rootPath, List<String> path) throws RepositoryException {
        Node root = session.getNode(rootPath);
        return ensurePathInternal(root, 0, path);
    }

    private Node ensurePathInternal(Node parent, int pos, List<String> path) throws RepositoryException {

        if (pos == path.size()) {
            return parent;
        }

        String element = path.get(pos);
        String elementSlug = slugify(element, removeStopWords(pos, path));

        Node next = parent.hasNode(elementSlug)
                ? parent.getNode(elementSlug)
                : folderNode(parent, element);
        int newPos = pos + 1;
        return ensurePathInternal(next, newPos, path);
    }

    /**
     * Should stopwords be removed when constructing this path element? We always remove stopwords unless this is
     * a publication type.  this is because the publications types were generated at a time when stopwords were always
     * included.
     */
    private boolean removeStopWords(int pos, List<String> path) {
        return pos == 4 && "Publications".equals(path.get(pos - 1));
    }

    public Node folderNode(Node parent, String name) throws RepositoryException {
        String slug = slugify(name);
        Node node = parent.addNode(slug, "hippostd:folder");
        node.addMixin("hippo:named");
        node.addMixin("mix:referenceable");
        node.addMixin("mix:versionable");
        node.addMixin("mix:simpleVersionable");
        node.addMixin("mix:lockable");
        node.setProperty("hippo:name", name);
        parent.setProperty("hippostd:hasfolders", true);
        return node;
    }

    public Node ensureImagePath(List<String> path) throws RepositoryException {
        Node root = session.getNode(IMG_ROOT);
        preventImagesAtGalleryRoot(root);
        return ensureImagePathInternal(root, 0, path);
    }

    /**
     * Prevents images (or new folders) being created directly in the gallery root via the CMS
     * UI, mirroring the same lockdown {@code scot.gov.www.OrganiseGalleryImagesJob} applies once
     * it has moved everything out of the root.
     */
    private void preventImagesAtGalleryRoot(Node root) throws RepositoryException {
        if (!Arrays.equals(readStringValues(root, "hippostd:foldertype"), EMPTY_STRINGS)) {
            root.setProperty("hippostd:foldertype", EMPTY_STRINGS);
        }
        if (!Arrays.equals(readStringValues(root, "hippostd:gallerytype"), EMPTY_STRINGS)) {
            root.setProperty("hippostd:gallerytype", EMPTY_STRINGS);
        }
    }

    private String[] readStringValues(Node node, String property) throws RepositoryException {
        if (!node.hasProperty(property)) {
            return EMPTY_STRINGS;
        }
        Value[] values = node.getProperty(property).getValues();
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = values[i].getString();
        }
        return strings;
    }

    private Node ensureImagePathInternal(Node parent, int pos, List<String> path) throws RepositoryException {
        if (pos == path.size()) {
            return parent;
        }

        String element = slugify(path.get(pos));
        String displayName = displayNameForContentFolder(path, pos, element);

        Node next = parent.hasNode(element)
                ? applyDisplayName(parent.getNode(element), displayName)
                : imageFolderNode(parent, element, displayName);
        int newPos = pos + 1;
        return ensureImagePathInternal(next, newPos, path);
    }

    /**
     * The elements of an image path are the actual node names of the mirrored content folder
     * chain under {@value #ROOT} (see {@code HippoUtils#pathFromNode}), so the display name for
     * each gallery folder can be read straight off the corresponding content folder's own
     * hippo:name, falling back to the raw path element if that folder has no name of its own.
     */
    private String displayNameForContentFolder(List<String> path, int pos, String fallback) throws RepositoryException {
        String contentPath = ROOT + String.join("/", path.subList(0, pos + 1));
        if (!session.nodeExists(contentPath)) {
            return fallback;
        }
        Node contentNode = session.getNode(contentPath);
        return contentNode.hasProperty("hippo:name")
                ? contentNode.getProperty("hippo:name").getString()
                : fallback;
    }

    private Node applyDisplayName(Node folder, String displayName) throws RepositoryException {
        if (!folder.hasProperty("hippo:name") || !displayName.equals(folder.getProperty("hippo:name").getString())) {
            folder.setProperty("hippo:name", displayName);
        }
        return folder;
    }

    private Node imageFolderNode(Node parent, String slug, String displayName) throws RepositoryException {
        Node node = parent.addNode(slug, "hippogallery:stdImageGallery");
        node.addMixin("hippo:named");
        node.addMixin("mix:referenceable");
        node.setProperty("hippo:name", displayName);
        parent.setProperty("hippostd:hasfolders", true);
        return node;
    }

}

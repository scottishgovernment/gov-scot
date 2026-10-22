package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import scot.gov.www.beans.LettersAndBeans;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class AtoZComponent extends BaseHstComponent {

    private static final int CHUNK_SIZE = 7;

    @Override
    public void doBeforeRender(
            final HstRequest request,
           final HstResponse response) {

        request.setAttribute("beansByLetter",
                computeLettersAndBeans(request.getRequestContext().getContentBean(), null));
    }

    /**
     * Groups the children of the given folder bean into the A-Z list structure. Exposed so that
     * callers which cannot rely on the request's content bean being the folder to list (for example
     * because a sitemap item's index page resolves the content bean to a document rather than its
     * containing folder) can resolve the correct folder themselves and pass it in directly.
     */
    public List<LettersAndBeans> computeLettersAndBeans(HippoBean folderBean) {
        return computeLettersAndBeans(folderBean, null);
    }

    /**
     * As {@link #computeLettersAndBeans(HippoBean)}, but excludes a specific child (by identifier)
     * from the listing - for example the folder's own index document, which should not list itself.
     */
    public List<LettersAndBeans> computeLettersAndBeans(HippoBean folderBean, HippoBean exclude) {
        SortedMap<String, List<HippoBean>> beansByFirstLetter = beansByFirstLetter(folderBean, exclude);
        List<LettersAndBeans> lettersAndBeans = lettersAndBeans(beansByFirstLetter);
        addLabels(lettersAndBeans);
        return lettersAndBeans;
    }

    private  List<LettersAndBeans> lettersAndBeans(SortedMap<String, List<HippoBean>> beansByFirstLetter) {
        // convert the map into a list of chunks of at least the chunk size
        LettersAndBeans current = new LettersAndBeans();
        List<LettersAndBeans> lettersAndBeans = new ArrayList<>();
        for (Map.Entry<String, List<HippoBean>> entry : beansByFirstLetter.entrySet()) {
            if (current.getBeans().size() >= CHUNK_SIZE) {
                lettersAndBeans.add(current);
                current = new LettersAndBeans();
            }
            current.getLetters().add(entry.getKey());
            current.getBeans().addAll(entry.getValue());
        }

        // if current contains no beans then append its letters to the last set, unless there is no
        // last set (i.e. no beans were found at all), in which case just keep the empty current set.
        if (current.getBeans().isEmpty() && !lettersAndBeans.isEmpty()) {
            LettersAndBeans last = lettersAndBeans.get(lettersAndBeans.size() - 1);
            last.getLetters().addAll(current.getLetters());
        } else {
            lettersAndBeans.add(current);
        }

        // sort each sublist.
        lettersAndBeans.stream()
                .map(LettersAndBeans::getBeans)
                .forEach(list -> list.sort(comparing(item -> item.getSingleProperty("govscot:title"))));

        return lettersAndBeans;
    }

    private void addLabels(List<LettersAndBeans> lettersAndBeans) {
        lettersAndBeans.stream().forEach(this::addLabel);
    }

    private void addLabel(LettersAndBeans lettersAndBeans) {
        String label = "";
        if (lettersAndBeans.getLetters().size() == 1) {
            label = lettersAndBeans.getLetters().first();
        } else {
            label = String.format("%s-%s", lettersAndBeans.getLetters().first(), lettersAndBeans.getLetters().last());
        }
        lettersAndBeans.setLabel(label);
    }

    private SortedMap<String, List<HippoBean>> beansByFirstLetter(HippoBean folderBean, HippoBean exclude) {
        List<HippoBean> beans = folderBean == null
                ? Collections.emptyList()
                : folderBean
                .getChildBeans(HippoBean.class)
                .stream()
                .filter(bean -> exclude == null || !bean.getIdentifier().equals(exclude.getIdentifier()))
                .map(this::determineBean)
                .filter(Objects::nonNull)
                .filter(this::hasTitle)
                .collect(toList());

        // group by first letter of the title.
        SortedMap<String, List<HippoBean>> map
                = new TreeMap<>(beans.stream().collect(groupingBy(bean -> firstLetter(bean))));

        // now ensure that every letter is included in the map
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars()
                .mapToObj(Character::toChars)
                .map(String::new)
                .forEach(letter -> map.putIfAbsent(letter, emptyList()));

        return map;
    }

    private HippoBean determineBean(HippoBean bean) {
        if (!bean.isHippoFolderBean()) {
            return bean;
        }

        HippoBean indexBean = bean.getBean("index");
        return indexBean == null ? null : indexBean;
    }

    private boolean hasTitle(HippoBean bean) {
        String title = bean.getSingleProperty("govscot:title");
        return title != null && !title.isEmpty();
    }

    private String firstLetter(HippoBean bean) {
        String title = bean.getSingleProperty("govscot:title");
        String upperCaseTitle = title.toUpperCase();
        char firstLetter = upperCaseTitle.charAt(0);
        return String.format("%s", firstLetter);
    }
}

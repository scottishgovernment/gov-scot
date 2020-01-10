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

        SortedMap<String, List<HippoBean>> beansByFirstLetter  = beansByFirstLetter(request);
        List<LettersAndBeans> lettersAndBeans = lettersAndBeans(beansByFirstLetter);
        addLabels(lettersAndBeans);
        request.setAttribute("beansByLetter", lettersAndBeans);
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

        // if current contains no beans then append its letters to the last set
        if (current.getBeans().isEmpty()) {
            LettersAndBeans last = lettersAndBeans.get(lettersAndBeans.size() - 1);
            last.getLetters().addAll(current.getLetters());
        } else {
            lettersAndBeans.add(current);
        }

        // sort each sublist.
        lettersAndBeans.stream()
                .map(LettersAndBeans::getBeans)
                .forEach(list -> list.sort(comparing(item -> item.getProperty("govscot:title"))));

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

    private SortedMap<String, List<HippoBean>> beansByFirstLetter(HstRequest request) {
        List<HippoBean> beans = request
                .getRequestContext()
                .getContentBean()
                .getChildBeans(HippoBean.class)
                .stream()
                .map(this::determineBean)
                .filter(Objects::nonNull)
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

    private String firstLetter(HippoBean bean) {
        String title = bean.getProperty("govscot:title");
        String upperCaseTitle = title.toUpperCase();
        char firstLetter = upperCaseTitle.charAt(0);
        return String.format("%s", firstLetter);
    }
}

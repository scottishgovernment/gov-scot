package scot.gov.www.searchjournal;

import org.apache.commons.lang3.RandomStringUtils;
import org.hippoecm.repository.util.DateTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * SearchJournal creating a record of actions that need to be taken in order to index content using funnelback.
 *
 * Maintained under /content/searchjournal
 */
public class SearchJournal {

    private static final Logger LOG = LoggerFactory.getLogger(SearchJournal.class);

    private final Session session;

    private static final String ACTION = "searchjournal:action";

    private static final String COLLECTION = "searchjournal:collection";

    private static final String URL = "searchjournal:url";

    private static final String TIMESTAMP = "searchjournal:timestamp";

    private static final String ATTEMPT = "searchjournal:attempt";

    public SearchJournal(Session session) {
        this.session = session;
    }

    public Node record(SearchJournalEntry entry) throws RepositoryException {
        Node record = getNodeForRecord(entry);
        LOG.info("record journal entry {} {} {}, attempt {}, {}",
                entry.getAction(), entry.getCollection(), entry.getUrl(), entry.getAttempt(), ((GregorianCalendar)entry.getTimestamp()).toZonedDateTime());
        record.setProperty(ACTION, entry.getAction());
        record.setProperty(COLLECTION, entry.getCollection());
        record.setProperty(URL, entry.getUrl());
        record.setProperty(TIMESTAMP, entry.getTimestamp());
        record.setProperty(ATTEMPT, entry.getAttempt());
        session.save();
        return record;
    }

    public SearchJournalEntry mostRecentEntry() throws RepositoryException {
        String xpath = "//element(*, searchjournal:entry) order by @searchjournal:timestamp descending";
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        query.setLimit(1);
        QueryResult queryResult = query.execute();
        if (!queryResult.getNodes().hasNext()) {
            return null;
        }
        Node node = queryResult.getNodes().nextNode();
        return entryForNode(node);
    }

    public List<SearchJournalEntry> getPendingEntries(Calendar position, int limit) throws RepositoryException {
        Query query = query(position, limit);
        QueryResult queryResult = query.execute();
        List<SearchJournalEntry> entries = new ArrayList<>();
        NodeIterator nodeIterator = queryResult.getNodes();

        while (nodeIterator.hasNext()) {
            Node entryNode = nodeIterator.nextNode();
            SearchJournalEntry entry = entryForNode(entryNode);
            entries.add(entry);
        }
        return entries;
    }

    Query query(Calendar position, int limit) throws RepositoryException {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(position.getTimeInMillis() + 1);
        String xpath = String.format("" +
                        "//element(*, searchjournal:entry)" +
                        "[@searchjournal:timestamp > %s][@searchjournal:timestamp <= %s] order by @searchjournal:timestamp",
                DateTools.createXPathConstraint(session, cal),
                DateTools.createXPathConstraint(session, Calendar.getInstance()));
        LOG.info("query: {}", xpath);
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        query.setLimit(limit);
        return query;
    }

    SearchJournalEntry entryForNode(Node node) throws RepositoryException {
        SearchJournalEntry entry = new SearchJournalEntry();
        entry.setAction(node.getProperty(ACTION).getString());
        entry.setCollection(node.getProperty(COLLECTION).getString());
        entry.setUrl(node.getProperty(URL).getString());
        entry.setTimestamp(node.getProperty(TIMESTAMP).getDate());
        entry.setAttempt(node.getProperty(ATTEMPT).getLong());
        return entry;
    }

    Node getNodeForRecord(SearchJournalEntry entry) throws RepositoryException {
        Node content = session.getNode("/content");
        Node searchjournal = ensurePathNode(content, "searchjournal");
        Calendar date = entry.getTimestamp();
        Node year = ensurePathNode(searchjournal, Integer.toString(date.get(Calendar.YEAR)));
        Node month = ensurePathNode(year, Integer.toString(date.get(Calendar.MONTH)));
        Node day = ensurePathNode(month, Integer.toString(date.get(Calendar.DAY_OF_MONTH)));
        String newName = uniquename(day);
        return day.addNode(newName, "searchjournal:entry");
    }

    Node ensurePathNode(Node parent, String name) throws RepositoryException {
        if (parent.hasNode(name)) {
            return parent.getNode(name);
        }
        return parent.addNode(name, "nt:unstructured");
    }

    String uniquename(Node parent) throws RepositoryException {
        String candidate = RandomStringUtils.randomAlphabetic(4);
        return parent.hasNode(candidate) ? uniquename(parent) : candidate;
    }

    public Session getSession() {
        return session;
    }
}
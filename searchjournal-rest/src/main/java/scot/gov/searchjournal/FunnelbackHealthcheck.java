package scot.gov.searchjournal;

import com.codahale.metrics.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hippoecm.repository.util.DateTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FunnelbackHealthcheck {

    private static final Logger LOG = LoggerFactory.getLogger(FunnelbackHealthcheck.class);

    private static final double EPSILON = 0.00001;

    private final Session session;

    public FunnelbackHealthcheck(Session session) {
        this.session = session;
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() throws Exception {
        HealthInfo healthinfo = new HealthInfo();
        addMetricsInformation(healthinfo);

        if (JournalPositionSource.getLastJournalPosition() != null) {
            addQueueInformation(healthinfo);
            setJournalPosition(healthinfo);
        }

        // decide what warnings and errors to generate
        healthinfo.setOk(healthinfo.getErrorList().isEmpty());

        return Response.status(200).entity(new ObjectMapper().writeValueAsString(healthinfo)).build();
    }

    void addMetricsInformation(HealthInfo healthinfo) {
        MetricRegistry metricRegistry = FunnelbackMetricRegistry.getInstance();
        Meter requestRate = metricRegistry.meter(MetricName.REQUEST_RATE.getMetricName());
        Meter errorRate = metricRegistry.meter(MetricName.ERROR_RATE.getMetricName());
        Meter failureRate = metricRegistry.meter(MetricName.FAILURE_RATE.getMetricName());
        Timer requestTimer = metricRegistry.timer(MetricName.REQUEST_TIMES.getMetricName());
        Timer jobTimer = metricRegistry.timer(MetricName.JOB_TIMES.getMetricName());
        healthinfo.setRequests(metricRegistry.counter(MetricName.REQUESTS.getMetricName()).getCount());
        healthinfo.setErrors(metricRegistry.counter(MetricName.ERRORS.getMetricName()).getCount());
        healthinfo.setFailures(metricRegistry.counter(MetricName.FAILURES.getMetricName()).getCount());
        healthinfo.setRequestRate(formatMeter(requestRate));
        healthinfo.setErrorRate(formatMeter(errorRate));
        healthinfo.setFailureRate(formatMeter(failureRate));
        healthinfo.setTimer(formatSnapshot(requestTimer.getSnapshot()));
        healthinfo.setJobTimer(formatSnapshot(jobTimer.getSnapshot()));

        // TODO: think we need to use an epsilon ... this is what we have in housing and the rate never seems to go to zerpo ....
        if (errorRate != null && errorRate.getFiveMinuteRate() > EPSILON) {
            healthinfo.getErrorList().add("Errors in the last 5 minutes");
        }

        if (failureRate != null && failureRate.getFiveMinuteRate() > EPSILON) {
            healthinfo.getErrorList().add("Failures in the last 15 minutes");
        }

        if (jobTimer.getFiveMinuteRate() > TimeUnit.MINUTES.toMillis(1)) {
            // in the last 15 minutes the loop is taking longer than a minute to complete
            healthinfo.getWarningList().add("Reconcilliation loop not completing within 1 minute in last 5 minutes");
        }
    }

    void addQueueInformation(HealthInfo healthinfo) {
        try {
            doAddQueueInfo(healthinfo);
        } catch (RepositoryException e) {
            LOG.error("RepositoryException trying to add queue information", e);
            healthinfo.getErrorList().add("RepositoryException trying to add queue information: " + e.getMessage());
        }
    }

    void doAddQueueInfo(HealthInfo healthinfo) throws RepositoryException {
        String xpath = outstandingJournalEntriesXpath();
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        QueryResult result = query.execute();

        /// break the q down in terms of number of attempts
        NodeIterator it = result.getNodes();
        Map<Long, Integer> attemptCounts = new HashMap<>();
        while (it.hasNext()) {
            Node node = it.nextNode();
            long attempt = node.getProperty("searchjournal:attempt").getLong();
            attemptCounts.putIfAbsent(attempt, 0);
            attemptCounts.put(attempt, attemptCounts.get(attempt).intValue() + 1);
        }
        healthinfo.setAttemptBreakdown(attemptCounts);
        healthinfo.setQueueSize(result.getRows().getSize());
    }

    String outstandingJournalEntriesXpath() {
        Calendar lastJournalPosition = JournalPositionSource.getLastJournalPosition();
        String dateConstraint = DateTools.createXPathConstraint(session, lastJournalPosition);
        String template = "//element(*, searchjournal:entry)[@searchjournal:timestamp > %s] order by @searchjournal:timestamp";
        return String.format(template, dateConstraint);
    }

    void setJournalPosition(HealthInfo healthinfo) {
        GregorianCalendar gregorianCalendar = (GregorianCalendar) JournalPositionSource.getLastJournalPosition();
        healthinfo.setJournalPosition(gregorianCalendar.toZonedDateTime().toString());
    }

    private String formatMeter(Metered m) {
        if (m == null) {
            return "null meter";
        }
        return String.format("count: %d, meanRate: %.02f, oneMinRate: %.02f, fiveMinRate: %.02f, fifteenMinRate: %.02f",
                m.getCount(), m.getMeanRate(),
                m.getOneMinuteRate(),  m.getFiveMinuteRate(), m.getFifteenMinuteRate() );
    }

    private String formatSnapshot(Snapshot ss) {
        return String.format(
                "min: %d, " +
                "max: %d, " +
                "mean: %.02f, " +
                "median: %.02f, " +
                "75th percentile: %.02f, " +
                "95th percentile: %.02f, " +
                "99th percentile: %.02f",
                toMillis(ss.getMin()),
                toMillis(ss.getMax()),
                toMillis(ss.getMean()),
                toMillis(ss.getMedian()),
                toMillis(ss.get75thPercentile()),
                toMillis(ss.get95thPercentile()),
                toMillis(ss.get99thPercentile()));
    }

    private long toMillis(long nanos) {
        return TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS);
    }

    private double toMillis(double nanos) {
        return nanos / TimeUnit.MILLISECONDS.toNanos(1);
    }

}

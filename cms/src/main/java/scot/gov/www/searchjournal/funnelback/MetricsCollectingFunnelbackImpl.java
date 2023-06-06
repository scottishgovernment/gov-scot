package scot.gov.www.searchjournal.funnelback;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import scot.gov.searchjournal.FunnelbackMetricRegistry;
import scot.gov.searchjournal.MetricName;

/**
 * Wrap a Funnlback opbject in order to collect metrics.
 */
public class MetricsCollectingFunnelbackImpl implements Funnelback {

    private final Funnelback funnelback;

    MetricRegistry metricsRegistry = FunnelbackMetricRegistry.getInstance();

    private final Timer requestTimes;

    private final Counter requestCounter;

    private final Counter errorCounter;

    private final Meter requestMeter;

    private final Meter errorMeter;

    MetricsCollectingFunnelbackImpl(Funnelback funnelback) {
        this.funnelback = funnelback;

        this.requestTimes = metricsRegistry.timer(MetricName.REQUEST_TIMES.getName());
        this.requestCounter = metricsRegistry.counter(MetricName.REQUESTS.getName());
        this.errorCounter = metricsRegistry.counter(MetricName.ERRORS.getName());
        this.requestMeter = metricsRegistry.meter(MetricName.REQUEST_RATE.getName());
        this.errorMeter = metricsRegistry.meter(MetricName.ERROR_RATE.getName());
    }

    @Override
    public void close() {
        funnelback.close();
    }

    @Override
    public void publish(String collection, String key, String html) throws FunnelbackException {
        try {
            Timer.Context timer = requestTimes.time();
            requestCounter.inc();
            requestMeter.mark();
            funnelback.publish(collection, key, html);
            timer.stop();
        } catch (FunnelbackException e) {
            errorCounter.inc();
            errorMeter.mark();
            throw e;
        }
    }

    @Override
    public void depublish(String collection, String key) throws FunnelbackException {
        try {
            Timer.Context timer = requestTimes.time();
            requestCounter.inc();
            requestMeter.mark();
            funnelback.depublish(collection, key);
            timer.stop();
        } catch (FunnelbackException e) {
            errorCounter.inc();
            errorMeter.mark();
            throw e;
        }
    }

    @Override
    public JournalPosition getJournalPosition() throws FunnelbackException {
        try {
            Timer.Context timer = requestTimes.time();
            requestCounter.inc();
            requestMeter.mark();
            JournalPosition position = funnelback.getJournalPosition();
            timer.stop();
            return position;
        } catch (FunnelbackException e) {
            errorCounter.inc();
            errorMeter.mark();
            throw e;
        }
    }

    @Override
    public void storeJournalPosition(JournalPosition position) throws FunnelbackException {
        try {
            Timer.Context timer = requestTimes.time();
            requestCounter.inc();
            requestMeter.mark();
            funnelback.storeJournalPosition(position);
            timer.stop();
        } catch (FunnelbackException e) {
            errorCounter.inc();
            errorMeter.mark();
            throw e;
        }
    }
}

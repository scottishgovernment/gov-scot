package scot.gov.searchjournal;

import com.codahale.metrics.MetricRegistry;

/**
 * Singleton containing the metrics registry for funnelback indexing.
 */
public class FunnelbackMetricRegistry {

    private static MetricRegistry metricRegistry = new MetricRegistry();

    public static MetricRegistry getInstance() {
        return metricRegistry;
    }
}

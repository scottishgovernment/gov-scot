package scot.gov.www;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;

/**
 * Records, on its own distinct logger, each page whose referenced image has been moved by
 * {@link OrganiseGalleryImagesJob}. Kept as a separate class (rather than logging through
 * {@code OrganiseGalleryImagesJob}'s own logger) so this specific event stream can be configured
 * — filtered, routed to its own appender, etc. — independently of the job's general logging.
 */
final class MovedImagePageLog {

    private static final Logger LOG = LoggerFactory.getLogger(MovedImagePageLog.class);

    private MovedImagePageLog() {
    }

    static void pageImageMoved(Session session, String pagePath, String imageFrom, String imageTo) {
        String url = PageUrls.bestEffortUrl(session, pagePath);
        if (url != null) {
            LOG.info("page {} ({}) references moved image: {} -> {}", pagePath, url, imageFrom, imageTo);
        } else {
            LOG.info("page {} references moved image: {} -> {}", pagePath, imageFrom, imageTo);
        }
    }
}

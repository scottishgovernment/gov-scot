package scot.gov.www;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Records, on its own distinct logger, every image found by {@link OrganiseGalleryImagesJob} to
 * be referenced by more than one document — split into the two cases the job treats
 * differently, so examples of each can be found independently of the job's general progress
 * logging: references that all belong to the same publication (filed in that publication's own
 * gallery folder) versus references that don't (filed under the shared
 * {@value OrganiseGalleryImagesJob#GENERAL_FOLDER} folder).
 */
final class MultiplyReferencedImageLog {

    private static final Logger LOG = LoggerFactory.getLogger(MultiplyReferencedImageLog.class);

    private MultiplyReferencedImageLog() {
    }

    static void samePublication(String imagePath, Set<String> handlePaths) {
        LOG.info("image {} is referenced by multiple pages/sections of the same publication: {}",
                imagePath, handlePaths);
    }

    static void differentPublications(String imagePath, Set<String> handlePaths) {
        LOG.info("image {} is referenced by multiple documents that are not all the same publication: {}",
                imagePath, handlePaths);
    }
}

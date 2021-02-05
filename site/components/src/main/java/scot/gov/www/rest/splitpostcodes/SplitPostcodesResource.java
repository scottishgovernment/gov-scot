package scot.gov.www.rest.splitpostcodes;

import com.google.common.base.Splitter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * Provide access to split postcode data
 *
 * The split postcodes data is stored in a csv file, splitPostcodes.csv.  This file is derived from the NRS data that
 * can be found here:
 * https://www.nrscotland.gov.uk/statistics-and-data/geography/nrs-postcode-extract
 * To update this data, open the latest postcode index file, filter it by the 'SplitIndicator' column to only include
 * split postcodes, and then save it as csv.  The nrs data gets updated twice a year.
 *
 * The postcodes in this file contain a letter at the end of the split.  When parsing the file we remove this and group
 * them by the postcode.  We retain the order that they apear in the file since this indicated which aplit is the most
 * populous.  We also filter out postcodes that are only split by what island they are on - if they have the same local
 * auth and ward then we do not count them as spit for these purposes.
 */
@Path("/split-postcodes/")
public class SplitPostcodesResource {

    private SortedMap<String, SplitPostcode> splitPostcodes = new TreeMap<>();

    public SplitPostcodesResource() throws IOException {
        // group the rows by postcode into a map
        Map<String, List<Geography>> splits = mapGeographiesByPostcode();

        // filter the map and build the format that supports the lookup
        splits.entrySet().stream().filter(this::include).forEach(this::add);
    }

    private Map<String, List<Geography>> mapGeographiesByPostcode() throws IOException {
        InputStream inputStream = SplitPostcodesResource.class.getResourceAsStream("/splitPostcodes.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        // ignore the header line
        String line = reader.readLine();
        Map<String, List<Geography>> splits = new HashMap<>();
        while (line != null) {
            addLine(splits, line);
            line = reader.readLine();
        }
        return splits;
    }

    private void addLine( Map<String, List<Geography>> splits, String line) {
        List<String> record = Splitter.on(',').splitToList(line);
        String postcodeWithSplitIndicator = record.get(0);
        String postcodeWithNoSplitIndicator = trim(removeLastChar(postcodeWithSplitIndicator));
        List<Geography> splitsForPostcodes = splits.getOrDefault(postcodeWithNoSplitIndicator, new ArrayList<>());
        Geography geography = new Geography();
        geography.setDistrict(record.get(2));
        geography.setWard(record.get(3));
        splitsForPostcodes.add(geography);
        splits.put(postcodeWithNoSplitIndicator, splitsForPostcodes);
    }

    private boolean include(Map.Entry<String, List<Geography>> entry) {
        List<Geography> splits = entry.getValue();
        return
                isSplitByCountry(splits) ||
                isSplitByDistrict(splits) ||
                isSplitByWard(splits);
    }

    private void add(Map.Entry<String, List<Geography>> entry) {
        SplitPostcode splitPostcode = new SplitPostcode(entry.getKey());
        splitPostcode.setSplits(entry.getValue());
        splitPostcodes.put(splitPostcode.getPostcode(), splitPostcode);
    }

    private String trim(String str) {
        return str.replace(" ", "");
    }

    private String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    private boolean isSplitByCountry(List<Geography> postcodes) {
        // if the postcode only appears once in the data then this means the postcode is split by the
        // Scotland / England border
        return postcodes.size() == 1;
    }

    private boolean isSplitByDistrict(List<Geography> postcodes) {
        int districtCount = postcodes.stream().map(Geography::getDistrict).collect(toSet()).size();
        return districtCount > 1;
    }

    private boolean isSplitByWard(List<Geography> postcodes) {
        int wardCount = postcodes.stream().map(Geography::getWard).collect(toSet()).size();
        return wardCount > 1;
    }

    /**
     * Get split postcode data for a postcode.  If a postcode is split it will return an object with a list of the
     * districts and wards that the postcode is split over.  The order indicates which spit is the most populous.
     *
     * Each split postcode will contain either 1 or 2 spits.  If it contains one split then this indidates that is
     * is split on the Scotland / England border.
     *
     * A non split postcode will return a 404.
     */
    @Path("{postcode}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public SplitPostcode getSplitPostcodes(@PathParam("postcode") String postcode) {
        return splitPostcodes.getOrDefault(postcode, new SplitPostcode(postcode));
    }

}

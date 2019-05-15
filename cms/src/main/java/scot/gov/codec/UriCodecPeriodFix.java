package scot.gov.codec;

import org.hippoecm.repository.api.StringCodecFactory;

import java.text.Normalizer;

public class UriCodecPeriodFix extends StringCodecFactory.UriEncoding {

    /**
     *  This class extends Hippo's built-in encoding functionality to
     *  escape periods/full stops. This is used when creating node/folder names.
     *  Periods are replaced with hyphens.
     *
     *  Hippo's page [1] explains how the built-in encoding works, and their
     *  javadoc [2] states that periods are removed. However, this is not the
     *  case as of 12.5.1. Periods aren't changed in any way.
     *
     *  As such, dots were appearing in URLs when content items use their
     *  folder name as the basis for this (i.e. most content, including complex
     *  documents, but excluding publications which use their slug).
     *
     *  [1] https://documentation.bloomreach.com/12/library/concepts/content-repository/node-name-encoding.html
     *  [2] https://javadoc.onehippo.org/11.0/hippo-repository/org/hippoecm/repository/api/doc-files/encoding.html
     *
     */

    @Override
    public String encode(final String input) {
        String encoded = super.encode(input);

        // replace all periods with hyphens:
        char[] chars = Normalizer.normalize(encoded, Normalizer.Form.NFC).toCharArray();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '.') {
                sb.append("-");
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }
}

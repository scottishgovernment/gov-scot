package scot.gov.redirects;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RedirectValidatorTest {

    RedirectValidator sut = new RedirectValidator();

    @Test
    public void rejectsEmptyFrom() {
        assertFalse(sut.validFrom(emptyFrom()));
    }

    @Test
    public void rejectsFromWithSpaces() {
        assertFalse(sut.validFrom(withFrom("/with spaces/")));
    }

    @Test
    public void rejectsFromWithoutLeadingSlashSpaces() {
        assertFalse(sut.validFrom(withFrom("noleadingslash")));
    }

    @Test
    public void rejectsFromWithinvalidChars() {
        assertFalse(sut.validFrom(withFrom("/invalidchar:")));
        assertFalse(sut.validFrom(withFrom("/invalidchar[")));
        assertFalse(sut.validFrom(withFrom("/invalidchar]")));
        assertFalse(sut.validFrom(withFrom("/invalidchar\t")));
        assertFalse(sut.validFrom(withFrom("/invalidchar\n")));
    }

    @Test
    public void acceptsValidFroms() {
        assertTrue(sut.validFrom(withFrom("/valid")));
        assertTrue(sut.validFrom(withFrom("/valideithparam?foo=bar")));
        assertTrue(sut.validFrom(withFrom("/valideithparams?foo=bar&q=bert")));
        assertTrue(sut.validFrom(withFrom("/UpperCaseLetters")));
        assertTrue(sut.validFrom(withFrom("/path/with-filename.pdf")));
        assertTrue(sut.validFrom(withFrom("/path/with-filename-and-params.pdf?foo=bar")));
    }

    @Test
    public void acceptsValidLocalTos() {
        assertTrue(sut.validTo(withTo("/localpath")));
        assertTrue(sut.validTo(withTo("/localpathUppercaseChars/")));
        assertTrue(sut.validTo(withTo("/localpath?with=param")));
        assertTrue(sut.validTo(withTo("/localpathWithFilename.pdf")));

        assertTrue(sut.validTo(withTo("https://www.google.com/")));
        assertTrue(sut.validTo(withTo("https://www.google.com/?q=searchterm")));
    }

    @Test
    public void validRedirectReturnNoViloations() {
        // ARRANGE
        Redirect redirect = new Redirect();
        redirect.setFrom("/from");
        redirect.setTo("/to");

        // ACT
        List<String> violations = sut.validateRedirects(Collections.singletonList(redirect));

        // ASSERT
        assertEquals(true, violations.isEmpty());
    }

    @Test
    public void invalidFromReturnsViloation() {
        // ARRANGE
        Redirect redirect = new Redirect();
        redirect.setFrom("invalidfrom");
        redirect.setTo("/to");

        // ACT
        List<String> violations = sut.validateRedirects(Collections.singletonList(redirect));

        // ASSERT
        assertEquals(violations.size(), 1);
        assertEquals(violations.get(0), "Invalid From url: invalidfrom");
    }

    @Test
    public void invalidToReturnsViloation() {
        // ARRANGE
        Redirect redirect = new Redirect();
        redirect.setFrom("/from");
        redirect.setTo("invalidto");

        // ACT
        List<String> violations = sut.validateRedirects(Collections.singletonList(redirect));

        // ASSERT
        Assert.assertTrue(violations.size() == 1);
        assertEquals(violations.get(0), "Invalid To url: invalidto");
    }
    Redirect emptyFrom() {
        return withFrom("");
    }

    Redirect withFrom(String from) {
        Redirect redirect = new Redirect();
        redirect.setFrom(from);
        return redirect;
    }

    Redirect withTo(String to) {
        Redirect redirect = new Redirect();
        redirect.setTo(to);
        return redirect;
    }
}

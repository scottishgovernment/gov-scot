package scot.gov.redirects;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
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

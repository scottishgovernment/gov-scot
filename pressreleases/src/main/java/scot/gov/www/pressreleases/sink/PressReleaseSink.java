package scot.gov.www.pressreleases.sink;

import scot.gov.www.pressreleases.domain.PressRelease;

import javax.jcr.RepositoryException;

public interface PressReleaseSink {

    void acceptPressRelease(PressRelease pressrelease) throws RepositoryException;

    void removeDeletedPressRelease(String id) throws RepositoryException;
}

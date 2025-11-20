package scot.gov.www.importer.sink;

import scot.gov.www.importer.domain.PressRelease;

import javax.jcr.RepositoryException;

public interface ContentSink {

    void acceptPressRelease(PressRelease pressrelease) throws RepositoryException;

    void removeDeletedPressRelease(String id) throws RepositoryException;
}

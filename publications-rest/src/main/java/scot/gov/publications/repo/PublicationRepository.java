package scot.gov.publications.repo;

public interface PublicationRepository {
    void create(Publication publication) throws PublicationRepositoryException;
    void update(Publication publication) throws PublicationRepositoryException;
    Publication get(String id) throws PublicationRepositoryException;
    ListResult list(Pageable pageable, PublicationListQuery query) throws PublicationRepositoryException;
}

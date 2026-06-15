package scot.gov.publications.repo;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL-backed implementation of PublicationRepository using Apache Commons DBUtils.
 */
public class PublicationRepositorySqlImpl implements PublicationRepository {

    QueryRunner queryRunner;

    Clock clock;

    QueryLoader queryLoader = new QueryLoader();

    String insertSQL;

    String updateSQL;

    String listSQL;

    public PublicationRepositorySqlImpl(QueryRunner queryRunner, Clock clock) {
        this.queryRunner = queryRunner;
        this.clock = clock;
        insertSQL = queryLoader.loadSQL("/sql/insert.sql");
        updateSQL = queryLoader.loadSQL("/sql/update.sql");
        listSQL = queryLoader.loadSQL("/sql/list.sql");
    }

    @Override
    public void create(Publication publication) throws PublicationRepositoryException {
        try {
            queryRunner.update(insertSQL, insertQueryArgs(publication));
        } catch (SQLException e) {
            throw new PublicationRepositoryException("Failed to create publication", e);
        }
    }

    @Override
    public void update(Publication publication) throws PublicationRepositoryException {
        publication.setLastmodifieddate(Timestamp.from(clock.instant()));
        try {
            Object[] args = updateQueryArgs(publication);
            queryRunner.update(updateSQL, args);
        } catch (SQLException e) {
            throw new PublicationRepositoryException("Failed to update publication", e);
        }
    }

    @Override
    public Publication get(String id) throws PublicationRepositoryException {
        String sql = "SELECT * FROM publication WHERE id = ?";
        try {
            return queryRunner.query(sql, new BeanHandler<>(Publication.class), id);
        } catch (SQLException e) {
            throw new PublicationRepositoryException("Failed to get publication", e);
        }
    }

    @Override
    public ListResult list(int page, int size, String title, String isbn, String filename)
            throws PublicationRepositoryException {
        BeanListHandler<Publication> handler = new BeanListHandler<>(Publication.class);
        try {
            List<Object> whereArgs = new ArrayList<>();
            StringBuilder whereClause = new StringBuilder();
            if (StringUtils.isNotBlank(title)) {
                whereClause.append("LOWER(title) LIKE ? AND ");
                whereArgs.add(like(title));
            }

            if (StringUtils.isNotBlank(isbn)) {
                whereClause.append("LOWER(isbn) LIKE ? AND ");
                whereArgs.add(like(isbn));
            }

            if (StringUtils.isNotBlank(filename)) {
                whereClause.append("LOWER(filename) LIKE ? AND ");
                whereArgs.add(like(filename));
            }

            whereClause.append("true");

            List<Object> args = new ArrayList<>();
            args.addAll(whereArgs);
            args.add(size);
            args.add((page - 1) * size);
            args.addAll(whereArgs);

            String sql = listSQL.replaceAll("<WHERECLAUSE>", whereClause.toString());
            List<Publication> publications = queryRunner.query(sql, handler, args.toArray());
            int totalsize = publications.isEmpty() ? 0 : publications.get(0).getFullcount();
            return ListResult.result(publications, totalsize, page, size);
        } catch (SQLException e) {
            throw new PublicationRepositoryException("Failed to list publications", e);
        }
    }

    private String like(String term) {
        return String.format("%%%s%%", term.toLowerCase());
    }

    private Object[] insertQueryArgs(Publication publication) {
        Timestamp now = Timestamp.from(clock.instant());
        return new Object[] {
                publication.getId(),
                publication.getUsername(),
                publication.getTitle(),
                publication.getIsbn(),
                publication.getFilename(),
                publication.getEmbargodate(),
                publication.getState(),
                publication.getStatedetails(),
                publication.getChecksum(),
                now,
                now,
                publication.getContact()
        };
    }

    private Object[] updateQueryArgs(Publication publication) {
        Timestamp now = Timestamp.from(clock.instant());
        return new Object[] {
                publication.getUsername(),
                publication.getTitle(),
                publication.getIsbn(),
                publication.getFilename(),
                publication.getEmbargodate(),
                publication.getState(),
                publication.getStatedetails(),
                publication.getChecksum(),
                now,
                publication.getContact(),
                publication.getId()
        };
    }
}

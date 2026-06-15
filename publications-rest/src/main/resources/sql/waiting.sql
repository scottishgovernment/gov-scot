SELECT * FROM publication
WHERE state in ('PENDING', 'PROCESSING')
ORDER BY lastmodifieddate DESC
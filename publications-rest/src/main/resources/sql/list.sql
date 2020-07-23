SELECT * FROM
    (SELECT * FROM publication WHERE <WHERECLAUSE> ORDER BY lastmodifieddate DESC LIMIT ? OFFSET ?) AS s1
JOIN
    (SELECT count(*) as fullcount FROM publication WHERE <WHERECLAUSE>) AS s2
ON true
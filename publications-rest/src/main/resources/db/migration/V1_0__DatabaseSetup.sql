CREATE TABLE publication
(
  id character varying(255) NOT NULL,
  username character varying(255) NOT NULL,
  title character varying NOT NULL,
  isbn character varying(25) NOT NULL,
  filename character varying(255) NOT NULL,
  embargodate timestamp,
  state character varying(20) NOT NULL,
  statedetails character varying,
  checksum character varying,
  createddate timestamp,
  lastmodifieddate timestamp,

  CONSTRAINT storage_pkey PRIMARY KEY (id)
);


BEGIN TRANSACTION;


CREATE TABLE IF NOT EXISTS StopType (
	_id                 INTEGER            NOT NULL, -- StopType.ordinal()
	name                NVARCHAR           NOT NULL UNIQUE, -- StopType.name()
	PRIMARY KEY(_id)
);

CREATE TABLE IF NOT EXISTS Stop (
	_id                 INTEGER            NOT NULL,
	name                NVARCHAR           NOT NULL,
	type                NVARCHAR           NOT NULL
	                                       CONSTRAINT "fk-Stop-StopType" REFERENCES StopType(_id),
	latitude            REAL               NOT NULL,
	longitude           REAL               NOT NULL,
	precision           INTEGER            NOT NULL,
	locality            NVARCHAR           NULL,
	address             NVARCHAR           NULL,
	telephone           NVARCHAR           NULL,
	PRIMARY KEY(_id)
);

CREATE TABLE IF NOT EXISTS Line (
	_id                 INTEGER            NOT NULL, -- Line.ordinal()
	name                NVARCHAR           NOT NULL UNIQUE, -- Line.name()
	PRIMARY KEY(_id)
);

CREATE TABLE IF NOT EXISTS Line_Stop (
	line                INTEGER            NOT NULL
	                                       CONSTRAINT "fk-Line_Stop-Line" REFERENCES Line(_id),
	stop                INTEGER            NOT NULL
	                                       CONSTRAINT "fk-Line_Stop-Stop" REFERENCES Stop(_id),
	code                NVARCHAR(4)        NULL,
	PRIMARY KEY(line, stop)
);

CREATE TABLE IF NOT EXISTS Route (
	_id                 NVARCHAR           NOT NULL,
	name                NVARCHAR           NOT NULL,
	line                INTEGER            NOT NULL
	                                       CONSTRAINT "fk-Route-Line" REFERENCES Line(_id),
	PRIMARY KEY(_id)
);

CREATE TABLE IF NOT EXISTS Section (
	_id                 NVARCHAR           NOT NULL,
	name                NVARCHAR           NOT NULL,
	PRIMARY KEY(_id)
);

CREATE TABLE IF NOT EXISTS Link (
	_id                 NVARCHAR           NOT NULL,
	name                NVARCHAR           NOT NULL,
	stopFrom            INTEGER            NOT NULL
	                                       CONSTRAINT "fk-Link_from-Stop" REFERENCES Stop(_id),
	stopTo              INTEGER            NOT NULL
	                                       CONSTRAINT "fk-Link_to-Stop" REFERENCES Stop(_id),
	distance            INTEGER            NOT NULL,
	PRIMARY KEY(_id)
);

CREATE TABLE IF NOT EXISTS Route_Section (
	route               NVARCHAR           NOT NULL
	                                       CONSTRAINT "fk-Route_Section-Route" REFERENCES Route(_id),
	section             NVARCHAR           NOT NULL
	                                       CONSTRAINT "fk-Route_Section-Section" REFERENCES Section(_id),
	seq                 INTEGER            NOT NULL,
	PRIMARY KEY(route, section)
);

CREATE TABLE IF NOT EXISTS Section_Link (
	section             NVARCHAR           NOT NULL
	                                       CONSTRAINT "fk-Section_Link-Section" REFERENCES Section(_id),
	link                NVARCHAR           NOT NULL
	                                       CONSTRAINT "fk-Section_Link-Link" REFERENCES Link(_id),
	seq                 INTEGER            NOT NULL,
	PRIMARY KEY(section, link)
);

CREATE TABLE IF NOT EXISTS AreaHull (
	area_code           VARCHAR(4)         NOT NULL,
	hull_index          INTEGER            NOT NULL,
	latitude            REAL               NOT NULL,
	longitude           REAL               NOT NULL,
	PRIMARY KEY(area_code, hull_index)
);

END TRANSACTION;
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

CREATE TABLE IF NOT EXISTS AreaHull (
	area_code           VARCHAR(4)         NOT NULL,
	hull_index          INTEGER            NOT NULL,
	latitude            REAL               NOT NULL,
	longitude           REAL               NOT NULL,
	PRIMARY KEY(area_code, hull_index)
);

END TRANSACTION;
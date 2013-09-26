BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS Station (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_id                 INTEGER            NOT NULL,
--	_company            INTEGER            NOT NULL DEFAULT 1
--	                                       CONSTRAINT "fk-Cinema-CinemaCompany" REFERENCES CinemaCompany(_id)
--	,
	name                NVARCHAR           NOT NULL UNIQUE,
	address             NVARCHAR           NULL,
	telephone           NVARCHAR           NULL,
	latitude            REAL               NULL,
	longitude           REAL               NULL,
	PRIMARY KEY(_id)
);

CREATE TABLE IF NOT EXISTS StationType (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_id                 INTEGER            NOT NULL,
	name                NVARCHAR           NOT NULL UNIQUE,
	url                 NVARCHAR           NULL,
	PRIMARY KEY(_id)
);

CREATE TRIGGER IF NOT EXISTS StationType_last_update
	AFTER UPDATE OF name, url ON StationType
	FOR EACH ROW
	WHEN
		    OLD.name <> NEW.name
		 OR OLD.url <> NEW.url
	BEGIN
		UPDATE
			StationType
		SET
			__last_update = CURRENT_TIMESTAMP
		WHERE
			_id = NEW._id
		; -- NEOS
	END
;

END TRANSACTION;
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

END TRANSACTION;
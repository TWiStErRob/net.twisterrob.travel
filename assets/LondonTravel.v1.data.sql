BEGIN TRANSACTION;
INSERT INTO StationType (name, url) VALUES('tubeStyle',       'http://www.tfl.gov.uk/tfl-global/images/syndication/roundel-tube.png');
INSERT INTO StationType (name, url) VALUES('dlrStyle',        'http://www.tfl.gov.uk/tfl-global/images/syndication/roundel-dlr.png');
INSERT INTO StationType (name, url) VALUES('overgroundStyle', 'http://www.tfl.gov.uk/tfl-global/images/syndication/roundel-overground.png');
END TRANSACTION;

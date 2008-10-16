CREATE TABLE Annotation (
	id         VARCHAR(36) NOT NULL,
	baseObject VARCHAR(36) NOT NULL,
    sortOf     VARCHAR(32) NOT NULL,

	PRIMARY KEY (id)
);

ALTER TABLE Annotation ADD UNIQUE (id);
ALTER TABLE Annotation ADD FOREIGN KEY (baseObject) REFERENCES BaseObject(id) ON DELETE CASCADE;

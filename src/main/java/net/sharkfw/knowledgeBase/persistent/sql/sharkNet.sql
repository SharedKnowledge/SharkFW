/* SQL-Skript welches eine Datenbank fuer SharkNET erzeugt */
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS subject_identifier (
	id integer PRIMARY KEY autoincrement,
	identifier text,
	tag_id integer,
	FOREIGN KEY (tag_id) references semantic_tag(id) on delete cascade
);

CREATE TABLE IF NOT EXISTS tag_set (
	id integer PRIMARY KEY autoincrement,
	set_kind text,
	info_id integer,
	tag_id integer,
	direction integer,
	FOREIGN KEY (info_id) references information(id),
	FOREIGN KEY (tag_id) references semantic_tag(id)
);

CREATE TABLE IF NOT EXISTS semantic_tag(
	id integer PRIMARY KEY autoincrement,
	name text,
	system_property text,
	property text,
	tag_kind integer,
	wkt text,
	t_duration integer,
	t_start integer
);

CREATE TABLE IF NOT EXISTS address (
	id integer PRIMARY KEY autoincrement,
	address_name text,
	tag_id integer,
	FOREIGN KEY (tag_id) references semantic_tag(id)
);

CREATE TABLE IF NOT EXISTS relation (
	id integer PRIMARY KEY autoincrement,
	source_tag_id integer,
	target_tag_id integer,
	name text
);

CREATE TABLE IF NOT EXISTS information (
	id integer PRIMARY KEY autoincrement,
	content_type text,
	content_length integer,
	content_stream BLOB,
	name text,
	property text
);

CREATE TABLE IF NOT EXISTS knowledge_base (
	id integer PRIMARY KEY autoincrement,
	property text,
  owner_tag integer,
  FOREIGN KEY (owner_tag) references semantic_tag(id)
);

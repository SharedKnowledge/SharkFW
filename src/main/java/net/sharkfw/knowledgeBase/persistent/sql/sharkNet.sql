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
	set_kind text
);

CREATE TABLE IF NOT EXISTS semantic_tag(
	id integer PRIMARY KEY autoincrement,
	name text,
	system_property text,
	property text,
	tag_set integer,
	tag_kind text,
	wkt text,
	t_duration integer,
	t_start integer,
	FOREIGN KEY (tag_set) references tag_set(id)
);

CREATE TABLE IF NOT EXISTS address (
	id integer PRIMARY KEY autoincrement,
	address_name text,
	tag_id integer,
	FOREIGN KEY (tag_id) references semantic_tag(id)
);

CREATE TABLE IF NOT EXISTS semantic_net (
	id integer PRIMARY KEY autoincrement
);

CREATE TABLE IF NOT EXISTS relation (
	id integer PRIMARY KEY autoincrement,
	source_tag_id integer,
	target_tag_id integer,
	name text,
	semantic_net_id integer
	/*FOREIGN KEY (semantic_net_id) references semantic_net(id)*/
);

CREATE TABLE IF NOT EXISTS vocabulary (
	id integer PRIMARY KEY autoincrement,
	topic_set integer,
	type_set integer,
	peer_set integer,
	location_set integer,
	time_set integer
	/*FOREIGN KEY (semantic_net_id) references semantic_net(id)*/
);

CREATE TABLE IF NOT EXISTS asip_space (
	id integer PRIMARY KEY autoincrement,
	topic_set integer,
	type_set integer,
	receiver_set integer,
	approver_set integer,
	time_set integer,
	location_set integer,
	sender_peer_tag integer,
	direction integer,
	FOREIGN KEY (topic_set) references tag_set(id),
	FOREIGN KEY (type_set) references tag_set(id),
	FOREIGN KEY (receiver_set) references tag_set(id),
	FOREIGN KEY (approver_set) references tag_set(id),
	FOREIGN KEY (time_set) references tag_set(id),
	FOREIGN KEY (location_set) references tag_set(id),
	FOREIGN KEY (sender_peer_tag) references peer_semantic_tag(id)
);

CREATE TABLE IF NOT EXISTS knowledge (
  id integer PRIMARY KEY autoincrement,
  vocabulary integer
);

CREATE TABLE IF NOT EXISTS asip_information_space (
	id integer PRIMARY KEY autoincrement,
	asip_space integer,
	knowledge integer,
	FOREIGN KEY (asip_space) references asip_space(id)
);

CREATE TABLE IF NOT EXISTS asip_information (
	id integer PRIMARY KEY autoincrement,
	content_type text,
	content_length integer,
	content_stream BLOB,
	name text,
	asip_information_space_id integer,
	FOREIGN KEY (asip_information_space_id) references asip_information_space(id)
);

/* SQL-Skript welches eine Datenbank fuer SharkNET erzeugt */
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS subject_identifier (
    id serial PRIMARY KEY,
    identifier text,
    tag_id integer
);

CREATE TABLE IF NOT EXISTS tag_set (
    id serial PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS semantic_tag(
    id serial PRIMARY KEY,
    name text,
	property text,
    tag_set integer,
	FOREIGN KEY (tag_set) references tag_set(id)	
);

CREATE TABLE IF NOT EXISTS peer_semantic_tag(
    id serial PRIMARY KEY,
    name text,
	property text,
    tag_set integer,
	FOREIGN KEY (id) references semantic_tag(id),
	FOREIGN KEY (tag_set) references tag_set(id)
);

CREATE TABLE IF NOT EXISTS time_semantic_tag(
    id serial PRIMARY KEY,
    name text,
	property text,
    t_duration integer,
	t_start integer,
	tag_set integer,
	FOREIGN KEY (id) references semantic_tag(id),
	FOREIGN KEY (tag_set) references tag_set(id)
);

CREATE TABLE IF NOT EXISTS spatial_semantic_tag(
    id serial PRIMARY KEY,
    name text,
	property text,
	coordinates Decimal(9,6),
    tag_set integer,
	FOREIGN KEY (id) references semantic_tag(id),
	FOREIGN KEY (tag_set) references tag_set(id)	
);

CREATE TABLE IF NOT EXISTS type_semantic_tag(
    id serial PRIMARY KEY,
    name text,
	property text,
    tag_set integer,
	FOREIGN KEY (id) references semantic_tag(id),
	FOREIGN KEY (tag_set) references tag_set(id)
);

CREATE TABLE IF NOT EXISTS address (
    id serial PRIMARY KEY,
    address_name text,
    tag_id integer,
	FOREIGN KEY (tag_id) references peer_semantic_tag(id)
);

CREATE TABLE IF NOT EXISTS semantic_net (
    id serial PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS relation (
    id serial PRIMARY KEY,
	source_tag_id integer,
	target_tag_id, integer,
	name text,
	semantic_net_id integer,
	FOREIGN KEY (semantic_net_id) references semantic_net(id)	
);

CREATE TABLE IF NOT EXISTS asip_space (
    id serial PRIMARY KEY,
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

CREATE TABLE IF NOT EXISTS asip_information_space (
    id serial PRIMARY KEY,
	asip_space integer,
	FOREIGN KEY (asip_space) references asip_space(id)
);

CREATE TABLE IF NOT EXISTS asip_information (
    id serial PRIMARY KEY,
	content_type text,
	content_length integer,
	content_stream BLOB,
	name text,
	asip_information_space_id integer,
	FOREIGN KEY (asip_information_space_id) references asip_information_space(id)
);

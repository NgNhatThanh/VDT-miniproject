CREATE DATABASE keycloak WITH OWNER = admin ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION
LIMIT = -1;

CREATE DATABASE meeting WITH OWNER = admin ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION
LIMIT = -1;

CREATE DATABASE document WITH OWNER = admin ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION
LIMIT = -1;

CREATE DATABASE meeting_history WITH OWNER = admin ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION
LIMIT = -1;

\c meeting

create table meetings
(
     id          integer generated always as identity
         constraint meetings_pk
         primary key,
     title       varchar(200) not null,
     start_time  timestamp    not null,
     end_time    timestamp    not null,
     description varchar,
     created_at  timestamp default current_timestamp,
     created_by  varchar      not null,
     updated_at  timestamp,
     location_id integer      not null,
     updated_by varchar
 );

create table meeting_locations
(
    id          integer generated always as identity
        constraint meeting_locations_pk
            primary key,
    name        varchar(100) not null,
    description varchar      not null,
     created_at  timestamp default current_timestamp,
     created_by  varchar      not null,
     updated_at  timestamp,
     updated_by varchar
);

create table meeting_roles
(
    id          integer generated always as identity
        constraint meeting_roles_pk
            primary key,
    name        varchar not null,
    description varchar,
    created_at  timestamp,
    created_by  varchar,
    updated_at  timestamp,
    updated_by  varchar
);

create table meeting_permissions
(
    id          integer generated always as identity
        constraint meeting_permissions_pk
            primary key,
    name        varchar not null,
    description varchar,
    created_at  timestamp,
    created_by  varchar,
    updated_at  timestamp,
    updated_by  varchar
);

create table meeting_role_has_permissions
(
    permission_id integer
        constraint meeting_role_has_permissions_meeting_permissions_id_fk
            references meeting_permissions,
    role_id       integer
        constraint meeting_role_has_permissions_meeting_roles_id_fk
            references meeting_roles
);

create table user_join_meeting
(
    id         integer generated always as identity
        constraint user_join_meeting_pk
            primary key,
    user_id    varchar not null,
    meeting_id integer
        constraint user_join_meeting_meetings_id_fk
            references meetings,
    status     varchar not null,
    role_id    integer
        constraint user_join_meeting_meeting_roles_id_fk
            references meeting_roles,
    reject_reason varchar,
    created_at  timestamp,
    created_by  varchar,
    updated_at  timestamp,
    updated_by  varchar
);

create table meeting_join_roles
(
    join_id integer
        constraint meeting_join_roles_user_join_meeting_id_fk
            references user_join_meeting,
    role_id integer
        constraint meeting_join_roles_meeting_roles_id_fk
            references meeting_roles
);

create table meeting_join_authorization
(
    id            integer generated always as identity
        constraint meeting_join_authorization_pk
            primary key,
    join_id       integer
        constraint meeting_join_authorization_user_join_meeting_id_fk
            references user_join_meeting,
    authorized_id varchar not null,
    created_at  timestamp,
    created_by  varchar,
    updated_at  timestamp,
    updated_by  varchar
);

create table meeting_private_notes
(
    id         integer generated always as identity
        constraint meeting_private_notes_pk
            primary key,
    meeting_id integer
        constraint meeting_private_notes_meetings_id_fk
            references meetings,
    content    varchar not null,
    created_at timestamp,
    created_by varchar,
    updated_at timestamp,
    updated_by varchar
);

create table meeting_documents
(
    id          integer generated always as identity
        constraint meeting_documents_pk
            primary key,
    status      varchar not null,
    meeting_id  integer references meetings(id),
    document_id integer,
    approved_by varchar not null,
    created_at  timestamp,
    created_by  varchar,
    updated_at  timestamp,
    updated_by  timestamp
);

insert into meeting_roles(name) values ('GUEST'), ('SECRETARY'), ('PARTICIPANT'), ('DOCUMENT_APPROVER') , ('HOST');

\c document

create table documents
(
    id         integer generated always as identity
        constraint documents_pk
            primary key,
    name       varchar not null,
    url        varchar not null,
    size       integer not null,
    created_at timestamp,
    created_by varchar,
    updated_at timestamp,
    updated_by varchar
);

\c meeting_history

create table meeting_histories
(
    id              integer generated always as identity
        constraint meeting_histories_pk
        primary key,
    meeting_id      integer not null,
    content         varchar,
    type            varchar not null,
    created_at      timestamp,
    created_by      varchar,
    updated_at      timestamp,
    updated_by      varchar
);
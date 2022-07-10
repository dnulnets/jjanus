    alter table if exists identity 
       drop constraint if exists FK1dv9rxvafjnhvsvowpx1si4d7;

    alter table if exists person_role 
       drop constraint if exists FKmi8mvq36xt0aceh3koph6kd1m;

    alter table if exists person_role 
       drop constraint if exists FKc3qnn28ihixub07wcywa6hreo;

    drop table if exists identity cascade;

    drop table if exists person cascade;

    drop table if exists person_role cascade;

    drop table if exists role cascade;

    create table identity (
       id uuid not null,
        identity varchar(64) not null,
        user_id uuid not null,
        primary key (id)
    );

    create table person (
       id uuid not null,
        credential varchar(128) not null,
        email varchar(64) not null,
        name varchar(64) not null,
        primary key (id)
    );

    create table person_role (
       id uuid not null,
        name uuid not null,
        primary key (id, name)
    );

    create table role (
       id uuid not null,
        description varchar(256),
        name varchar(64) not null,
        primary key (id)
    );

    alter table if exists identity 
       add constraint UK_rk48ronlx09yf9q4ptmiyxhve unique (identity);

    alter table if exists role 
       add constraint UK_8sewwnpamngi6b1dwaa88askk unique (name);

    alter table if exists identity 
       add constraint FK1dv9rxvafjnhvsvowpx1si4d7 
       foreign key (user_id) 
       references person;

    alter table if exists person_role 
       add constraint FKmi8mvq36xt0aceh3koph6kd1m 
       foreign key (name) 
       references role;

    alter table if exists person_role 
       add constraint FKc3qnn28ihixub07wcywa6hreo 
       foreign key (id) 
       references person;
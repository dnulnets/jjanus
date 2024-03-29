--
-- Create the database tables
--

    create table backlog (
       id uuid not null,
        primary key (id)
    );

    create table backlog_backlogitem (
       "backlog" uuid not null,
        backlogitem uuid not null,
        primary key ("backlog", backlogitem)
    );

    create table backlogitem (
       id uuid not null,
        position int4 not null,
        primary key (id)
    );

    create table product (
       id uuid not null,
        description varchar(255),
        name varchar(255) not null,
        current uuid,
        primary key (id)
    );

    create table productstate (
       id uuid not null,
        display varchar(255) not null,
        primary key (id)
    );

    create table productversion (
       id uuid not null,
        closed boolean not null,
        version varchar(255) not null,
        product uuid not null,
        state uuid,
        primary key (id)
    );

    create table "role" (
       id uuid not null,
        description varchar(255),
        longName varchar(255) not null,
        name varchar(255) not null,
        primary key (id)
    );

    create table team (
       id uuid not null,
        name varchar(255) not null,
        backlog uuid not null,
        primary key (id)
    );

    create table team_product (
       team uuid not null,
        product uuid not null,
        primary key (team, product)
    );

    create table team_user (
       team uuid not null,
        "user" uuid not null,
        primary key (team, "user")
    );

    create table "user" (
       id uuid not null,
        email varchar(255) not null,
        name varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    );

    create table user_role (
       "user" uuid not null,
        role uuid not null,
        primary key ("user", role)
    );

    alter table if exists backlog_backlogitem 
       add constraint UK_knlxrwa568mpnj9e9nurne6th unique (backlogitem);

    alter table if exists backlogitem 
       add constraint UK_ooutc69lq8t9tg50qgpy5cxj1 unique (position);

    alter table if exists product 
       add constraint UK_jmivyxk9rmgysrmsqw15lqr5b unique (name);

    alter table if exists productstate 
       add constraint UK_4h3l89tiwppnue161gonm8m95 unique (display);

    alter table if exists productversion 
       add constraint UK1595wlh0xqcdk43cu58aghl7n unique (version, product);

    alter table if exists "role" 
       add constraint UK_o8bfng039ihyylu5vhv60143a unique (longName);

    alter table if exists "role" 
       add constraint UK_8sewwnpamngi6b1dwaa88askk unique (name);

    alter table if exists team 
       add constraint UK_g2l9qqsoeuynt4r5ofdt1x2td unique (name);

    alter table if exists "user" 
       add constraint UK_sb8bbouer5wak8vyiiy4pf2bx unique (username);

    alter table if exists backlog_backlogitem 
       add constraint FKn4d48iikfs1rae7vlaxfibi47 
       foreign key (backlogitem) 
       references backlogitem;

    alter table if exists backlog_backlogitem 
       add constraint FKh0pyown32g3gpvjubfybrt96k 
       foreign key ("backlog") 
       references backlog;

    alter table if exists product 
       add constraint FKgar6lll0fbbd8h9i3c8wqpiq2 
       foreign key (current) 
       references productversion;

    alter table if exists productversion 
       add constraint FK66derwhwsbodq2gi7khbu8qxf 
       foreign key (product) 
       references product;

    alter table if exists productversion 
       add constraint FKoyd2sxichvysmv7e6wnjaafh9 
       foreign key (state) 
       references productstate;

    alter table if exists team 
       add constraint FKeh2mk594huskwm5cqjt3n9kmn 
       foreign key (backlog) 
       references backlog;

    alter table if exists team_product 
       add constraint FKg0fetp05dxl349gmum4bfuqty 
       foreign key (product) 
       references product;

    alter table if exists team_product 
       add constraint FKh311j8n8ftes8726l72qhccf7 
       foreign key (team) 
       references team;

    alter table if exists team_user 
       add constraint FKs02b7rasvcyl6nxl352mik3d7 
       foreign key ("user") 
       references "user";

    alter table if exists team_user 
       add constraint FK6qoosh2nst8y9kmfu9r2rxweh 
       foreign key (team) 
       references team;

    alter table if exists user_role 
       add constraint FKfvosxogs5j57ymatc75n98psx 
       foreign key (role) 
       references "role";

    alter table if exists user_role 
       add constraint FKte5t6n42fa7onvfamnqrgqg8b 
       foreign key ("user") 
       references "user";

--
-- Create default roles
--
insert into role values (gen_random_uuid(), 'A user responsible for the products entire life cycle', 'Product owner', 'product');
insert into role values (gen_random_uuid(), 'A user supporting a team and a product', 'User', 'user');
insert into role values (gen_random_uuid(), 'A user responsible for the administration of the Janus system', 'Administrator', 'admin');
insert into role values (gen_random_uuid(), 'An authenticated user', 'Anyone', 'any');

--
-- Create the default administrator
--
create extension if not exists pgcrypto;
insert into "user" (id, email, name, password, username) 
   values (
      gen_random_uuid(), 
      'admin@changeme.com', 
      'Administrator', 
      crypt('admin', gen_salt('bf', 10)),
      'admin');
insert into user_role ("user", role) values (
   (select id from "user" where username ='admin'),
   (select id from role where name='admin'));
insert into user_role ("user", role) values (
   (select id from "user" where username ='admin'),
   (select id from role where name='any'));

--
-- Create the default available product states
--
insert into productstate values (gen_random_uuid(), 'Pre-alpha');
insert into productstate values (gen_random_uuid(), 'Alpha');
insert into productstate values (gen_random_uuid(), 'Feature Complete (FC)');
insert into productstate values (gen_random_uuid(), 'Beta');
insert into productstate values (gen_random_uuid(), 'Perpetual beta');
insert into productstate values (gen_random_uuid(), 'Open Beta');
insert into productstate values (gen_random_uuid(), 'Closed beta');
insert into productstate values (gen_random_uuid(), 'Release Candidate (RC)');
insert into productstate values (gen_random_uuid(), 'Stable Release');
insert into productstate values (gen_random_uuid(), 'Production Release');
insert into productstate values (gen_random_uuid(), 'Release to manufacturing (RTM)');
insert into productstate values (gen_random_uuid(), 'Release to the Web (RTW)');
insert into productstate values (gen_random_uuid(), 'End of life (EOL)');


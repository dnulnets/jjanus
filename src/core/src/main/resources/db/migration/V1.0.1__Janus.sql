    create table role (
       id uuid not null,
        description varchar(256),
        longName varchar(64) not null,
        name varchar(64) not null,
        primary key (id)
    );

    create table "user" (
       id uuid not null,
        email varchar(64) not null,
        name varchar(64) not null,
        password varchar(128) not null,
        username varchar(64) not null,
        primary key (id)
    );

    create table user_role (
       "user" uuid not null,
        role uuid not null,
        primary key ("user", role)
    );

    alter table if exists role 
       add constraint UK_o8bfng039ihyylu5vhv60143a unique (longName);

    alter table if exists role 
       add constraint UK_8sewwnpamngi6b1dwaa88askk unique (name);

    alter table if exists "user" 
       add constraint UK_sb8bbouer5wak8vyiiy4pf2bx unique (username);

    alter table if exists user_role 
       add constraint FK26f1qdx6r8j1ggkgras9nrc1d 
       foreign key (role) 
       references role;

    alter table if exists user_role 
       add constraint FKte5t6n42fa7onvfamnqrgqg8b 
       foreign key ("user") 
       references "user";

insert into role values (gen_random_uuid(), 'A user responsible for the products entire life cycle', 'Product owner', 'product');
insert into role values (gen_random_uuid(), 'A user responsible for the service entire life cycle', 'Service owner', 'service');
insert into role values (gen_random_uuid(), 'A user acting as a team leader', 'Team leader', 'team');
insert into role values (gen_random_uuid(), 'A user acting as a team member', 'Team member', 'member');
insert into role values (gen_random_uuid(), 'A user responsible for the administration of the Janus system', 'Administrator', 'admin');
insert into role values (gen_random_uuid(), 'An authenticated user', 'Anyone', 'any');


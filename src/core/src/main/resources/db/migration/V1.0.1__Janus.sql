create table role (
   id uuid not null,
   description varchar(256),
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

insert into role values (gen_random_uuid(), 'The generic Janus user', 'user');
insert into role values (gen_random_uuid(), 'The Janus administrator', 'admin');
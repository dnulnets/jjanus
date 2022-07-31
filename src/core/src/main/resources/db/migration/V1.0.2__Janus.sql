create extension if not exists pgcrypto;
insert into "user" (id, email, name, password, username) 
   values (
      gen_random_uuid(), 
      'tomas.stenlund@telia.com', 
      'Tomas Stenlund', 
      crypt('mandelmassa', gen_salt('bf', 10)),
      'tomas');
insert into user_role ("user", role) values (
   (select id from "user" where username ='tomas'),
   (select id from role where name='admin'));
insert into user_role ("user", role) values (
   (select id from "user" where username ='tomas'),
   (select id from role where name='user'));
insert into user_role ("user", role) values (
   (select id from "user" where username ='tomas'),
   (select id from role where name='any'));

    alter table if exists backlog_backlogitem 
       drop constraint if exists FKn4d48iikfs1rae7vlaxfibi47;

    alter table if exists backlog_backlogitem 
       drop constraint if exists FKh0pyown32g3gpvjubfybrt96k;

    alter table if exists team 
       drop constraint if exists FKeh2mk594huskwm5cqjt3n9kmn;

    alter table if exists team_user 
       drop constraint if exists FKs02b7rasvcyl6nxl352mik3d7;

    alter table if exists team_user 
       drop constraint if exists FK6qoosh2nst8y9kmfu9r2rxweh;

    alter table if exists user_role 
       drop constraint if exists FKfvosxogs5j57ymatc75n98psx;

    alter table if exists user_role 
       drop constraint if exists FKte5t6n42fa7onvfamnqrgqg8b;

    drop table if exists backlog cascade;

    drop table if exists backlog_backlogitem cascade;

    drop table if exists backlogitem cascade;

    drop table if exists product cascade;

    drop table if exists "role" cascade;

    drop table if exists team cascade;

    drop table if exists team_user cascade;

    drop table if exists "user" cascade;

    drop table if exists user_role cascade;

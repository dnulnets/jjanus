insert into backlog (id) values (gen_random_uuid());
insert into "team" (id, name, backlog)
   values (
      gen_random_uuid(), 
      'blackbird', '359aeaad-75c4-4ed2-9db8-d6e15963bb7e');
insert into team_user (team, "user")
    values (
        (select id from team where name='blackbird'),
        (select id from "user" where username='kungen')
    );
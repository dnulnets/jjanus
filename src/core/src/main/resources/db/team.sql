insert into backlog (id) values (gen_random_uuid());
insert into "team" (id, name, backlog)
   values (
      gen_random_uuid(), 
      'blackbird', '359aeaad-75c4-4ed2-9db8-d6e15963bb7e');
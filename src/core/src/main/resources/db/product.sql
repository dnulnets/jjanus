insert into "product" (id, name, description, current) 
   values (
      gen_random_uuid(),
      'SMP', 
      'Service Metadata Publisher for the e-Delivery service', 
      null);
insert into productversion (id, version, closed, state, product) values (
      gen_random_uuid(),
      'V1.0.0',
      false,
      (select id from "productstate" where display='Alpha'),
      (select id from "product" where name ='SMP'));

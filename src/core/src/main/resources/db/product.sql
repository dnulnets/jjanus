insert into "product" (id, name, description, current) 
   values (
      gen_random_uuid(),
      'SMP', 
      'Service Metadata Publisher for the e-Delivery service', 
      null);
insert into productversion (id, version, product) values (
      gen_random_uuid(),
      'V1.0.0',
      (select id from "product" where name ='SMP'));

# Janus

![Janus login page](/documentation/login.png)

An Application/Product Lifecycle Management solution handling products with regards to user needs, requirements, issues, teams, backlogs and releases for development, building and maintaining products and services, focusing on their entire life cycle.

**_NOTE:_** From **wikipedia**: In ancient Roman religion and myth, Janus (/ˈdʒeɪnəs/ JAY-nəs; Latin: Ianus [ˈi̯aːnʊs]) is the god of beginnings, gates, transitions, time, duality, doorways, passages, frames, and endings.

It will contain support for teams, product owners and single individuals tasks to maintain the product or service.

## Status
The project is in an early design and startup phase. It started 2022-06-06 and is currently working on a first sprint of the system design that will contain early sketches of the solution, information model and user interface to get started.

The first code has been created with a fully working RBAC security solution and form authentication that allows you to login based on users in the database and their roles.

* SSR framework for GUI elements, such as forms, buttons, links, tables, checkboxes, navigation created.
* Language support using @MessageBundles
* Session support via client side cookie
* Login and logout of the application
* Administration of users, their roles and team designation, i.e. list, view, create, update and delete. Requires role admin.
* Administration of teams and its members, i.e. list, view, create, update and delete. Requires role admin.
* Administration of product and versions of products, i.e. list, view, create, update and delete. Requires role admin or product owner.
* Administration of what product a team is responsible for

It is just the rest of the application left ;-) Look at the issues tab to see what is currently ongoing.

See [wiki](https://github.com/dnulnets/janus/wiki/)

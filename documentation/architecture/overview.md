```plantuml
@startuml

node "cluster" {
[proxy]

package "Frontend" {
  SPA-->[Janus FE]
}

package "Backend" {
  API-->[Janus BE]
}

package "IAM" {
  OIDC-->[Keycloak]
}

}

database "Postgresql" {

}

SMTP-->[Mailserver]
GIT-->[Repository]

[Janus BE] --> Postgresql
[Janus BE] --> OIDC
[Janus BE] --> SMTP
[Janus BE] --> GIT
[proxy] --> API
[proxy] --> SPA
[proxy] --> OIDC
HTTP --> proxy

@enduml
```
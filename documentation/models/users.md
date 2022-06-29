```mermaid
classDiagram
    class User {
        string name
        string email
        string credential
    }
    class Principal {
        string id
    }
    class ObjectRole {
        string objectType
    }   
    class Role {
        string id
        string name
        string description
    }
    User "*" --> "*" Role : has
    User "1" --> "1..*" Principal : identity
    ObjectRole "*" --> "1" Role : derivesFrom
    ObjectRole "*" <-- "*" User : has
    Any "1" <-- "1" ObjectRole : validFor
    Product --|> Any : inherits
    Service --|> Any : inherits
```
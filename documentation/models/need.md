```mermaid
classDiagram
    class UserNeed {
        string id
        string name
        string description
    }
    class Requirement {
        string id
        string name
        string description
    }
    Product "1" --> "*" UserNeed : has
    UserNeed "1" --> "*" Requirement : generates
    UserNeed "*" <-- "*" ProductVersion : supports
    Requirement "*" <-- "*" ProductVersion : implements
    Product "1" --> "*" Requirement : has
```
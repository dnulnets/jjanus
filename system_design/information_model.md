# Information model
The information model for the product.

## Services and products

```mermaid
classDiagram
    class Service {
        string name
    }
    class ServiceVersion {
        string version
    }
    Service "1" --> "1..*" ServiceVersion : provides
    class Product {
        string name
    }
    class ProductVersion {
        string version
    }
    Product "1" --> "1..*" ProductVersion : provides
    ServiceVersion "1" --> "*" ProductVersion : uses
```

## Issues 
```mermaid
erDiagram
    Issue {
        string id
        string title
        string description
    }
    IssueTemplate {
        string id
        string name
    }
    StateMachine {
        string id
        string startState
    }
    State {
        string id
        string name
    }
    Transition {
        string id
        string name
    }
    Attribute {
        string id
        string name
        string value
    }
    AttributeTemplate {
        string id
        string name
        string defaultValue
    }    
    Issue ||--o{ Issue: "subissues"
    Issue ||--o{ Attribute : "attributes"
    Issue ||--|| State : "state"
    Issue ||--|| IssueTemplate : "type"
    IssueTemplate ||--|| StateMachine : "machine"
    IssueTemplate ||--|{ State : "states"
    IssueTemplate ||--o{ AttributeTemplate : "attributes"
    State ||--|| Transition : "from"
    Transition ||--|| State : "to"
```

# Information model
The information model for the product.

## Services and products

```mermaid
classDiagram
    class Service {
        string name
    }
    class Product {
        string name
    }
    class ProductVersion {
        string version
    }
    Product "1" --> "1..*" ProductVersion : has
    Product "1" --> "1" ProductVersion : latest
    Service "1" --> "*" ProductVersion : uses
```

## Requirements
```mermaid
classDiagram
    class Product {
        string id
        string name
    }
    class ProductVersion {
        string version
    }
    class Requirement {
        string id
        string name
        string description
        RequirementType type
    }
    Product "1" --> "1..*" ProductVersion : has
    Product "1" --> "1" ProductVersion : latest
    ProductVersion "*" --> "*" Requirement : implements
    class RequirementType {
        <<enumeration>>
        Functional
        NonFunctional
    }
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

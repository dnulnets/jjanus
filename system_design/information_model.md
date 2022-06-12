# Information model
The logical information model for the application.

## Services and products

```mermaid
classDiagram
    class Service {
        string id
        string name
    }
    class Product {
        string id
        string name
        string description
    }
    class ProductVersion {
        string version
    }
    Product "1" --> "1..*" ProductVersion : has
    Product "1" --> "1" ProductVersion : latest
    Service "1" --> "*" ProductVersion : uses
```
### Product

|Name|Type|Value Set|Description|   |
|---|---|---|---|---|
|id|string|alfanumeric|The identity of the product, typical a shortening of the name|   |
|name|string|alfanumeric|The name of the product|   |
|description|string|alfanumeric|A description of the product|   |


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

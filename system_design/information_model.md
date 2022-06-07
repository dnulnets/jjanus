# Information model
The information model for the product.

## Services and products

```mermaid
erDiagram
    Service ||--|{ ServiceVersion : "has"
    Service {
        string name
    }
    ServiceVersion {
        string version
    }
    ServiceVersion ||--o{ ProductVersion: "consists of"
    Product ||--|{ ProductVersion : "has"
    Product {
        string name
    }
    ProductVersion {
        string version
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
    Issue ||--|| State : "state"
    Issue ||--|| IssueTemplae : "type"
    IssueTemplate ||--|| StateMachine : "machine"
    IssueTemplate ||--|{ State : "state"
    State ||--|| Transition : "from"
    Transition ||--|| State : "to"
```

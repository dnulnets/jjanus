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

# Services and products

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
    Service "1" --> "*" ProductVersion : installed
```

## Product
A product is a software application that is released in different versions. They can be installed and run and is then considered a service or a part of a service.

|Name|Type|Value Set|Description|
|---|---|---|---|
|id|string|alfanumeric|The identity of the product, typical a shortening of the name|
|name|string|alfanumeric|The name of the product|
|description|string|alfanumeric|A description of the product|
|has|list of ProductVersion|fk|A list of all versions of the product|
|latest|ProductVersion|fk|The latest release of the product|

## ProductVersion
A product is released as a specific version. Most of the work related to a product is connected to its specific target version.

|Name|Type|Value Set|Description|
|---|---|---|---|
|version|string|alfanumeric|The version of the product, or release of a product|

## Service
A service is an installed product or products.

|Name|Type|Value Set|Description|
|---|---|---|---|
|id|string|alfanumeric|The identity of the service, typical a shortening of the name|
|name|string|alfanumeric|The name of the service|

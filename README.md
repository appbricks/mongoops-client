# MongoDB Dedicated Service Broker

## Development


### Testing Service Broker API Locally

* Retrieve catalog

  ```
  curl -X GET \
    -H "X-Broker-API-Version: 2.7" \
    -H "Content-Type: application/json" \
    -u user:m0ngodbaas! \
    http://localhost:8080/v2/catalog
  ```

* Create service instance

  - Instance ID: my-instance

  ```
  curl -X PUT \
    -H "X-Broker-API-Version: 2.7" \
    -H "Content-Type: application/json" \
    -u user:m0ngodbaas! \
    http://localhost:8080/v2/service_instances/my-instance?accepts_incomplete=true \
    -d '{
      "organization_guid": "my-org-guid",
      "space_guid": "my-space-guid",
      "service_id": "mongodb-dedicated",
      "plan_id": "development",
      "parameters": { "dbVersion": "3.0.7" }
    }'
  ```
  
* Check creation/update status

  - Instance ID: my-instance

  ```
  curl -X GET \
    -H "X-Broker-API-Version: 2.7" \
    -H "Content-Type: application/json" \
    -u user:m0ngodbaas! \
    http://localhost:8080/v2/service_instances/my-instance/last_operation?accepts_incomplete=true
  ```

* Update service instance

  - Instance ID: my-instance

  ```
  curl -X PATCH \
    -H "X-Broker-API-Version: 2.7" \
    -H "Content-Type: application/json" \
    -u user:m0ngodbaas! \
    http://localhost:8080/v2/service_instances/my-instance?accepts_incomplete=true \
    -d '{
      "service_id": "mongodb-dedicated",
      "plan_id": "development",
      "parameters": {
        "dbVersion": "3.2.3"
      },
      "previous_values": {
        "organization_guid": "my-org-guid",
        "space_guid": "my-space-guid",  
        "service_id": "mongodb-dedicated",
        "plan_id": "development"
      }
    }'
  ```

* Create service instance binding

  - Instance ID: my-instance
  - Binding ID: my_binding
  
  ```
  curl -X PUT \
    -H "X-Broker-API-Version: 2.7" \
    -H "Content-Type: application/json" \
    -u user:m0ngodbaas! \
    http://localhost:8080/v2/service_instances/my-instance/service_bindings/my_binding \
    -d '{
      "plan_id": "development",
      "service_id": "mongodb-dedicated",
      "app_guid": "my-app-guid",
      "parameters": { "databases": "db1,db2,db2" }
    }'
  ```

* Delete service instance binding

  - Instance ID: my-instance
  - Binding ID: my_binding
  
  ```
  curl -X DELETE \
    -H "X-Broker-API-Version: 2.7" \
    -H "Content-Type: application/json" \
    -u user:m0ngodbaas! \
    http://localhost:8080/v2/service_instances/my-instance/service_bindings/my_binding?service_id=mongodb-dedicated&plan_id=development&accepts_incomplete=true
  ```

* Delete service instance
  
  - Instance ID: my-instance

  ```
  curl -X DELETE \
    -H "X-Broker-API-Version: 2.7" \
    -H "Content-Type: application/json" \
    -u user:m0ngodbaas! \
    http://localhost:8080/v2/service_instances/my-instance?service_id=mongodb-dedicated&plan_id=development&accepts_incomplete=true
  ```
# mongoops-client

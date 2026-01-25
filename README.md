# event-driven-order-platform
## Problem Statement
Traditional synchronous order processing systems are tightly coupled
and fail under scale. This project demonstrates an event-driven
architecture using Kafka to decouple services and improve resilience.

# Event-Driven Order Platform (Spring Boot + Kafka + Postgres)

A production-style event-driven microservices project demonstrating async communication with Kafka, persistence with Postgres, idempotent consumers, and dead-letter topics.

## Services
- **order-service (8080)**: REST API to create orders, stores to Postgres, publishes `order.created`
- **payment-service (8081)**: consumes `order.created`, stores payment, publishes `payment.completed`
- order-service consumes `payment.completed` and updates order status to **PAID**

## Architecture
```mermaid
Client -->|POST /orders| OrderSvc[Order Service :8080]
OrderSvc -->|JPA| OrdersDB[(Postgres :5433)]
OrderSvc -->|publish| OC[(Kafka topic: order.created)]

OC -->|consume| PaySvc[Payment Service :8081]
PaySvc -->|JPA| PayDB[(Postgres :5433)]
PaySvc -->|publish| PC[(Kafka topic: payment.completed)]

PC -->|consume| OrderSvc
OrderSvc -->|update status=PAID| OrdersDB

OC --> DLT[(order.created.DLT)]
```
## Kafka Topics
 * order.created
 * payment.completed
 * order.created.DLT (Dead Letter Topic)

## Quick Start
1) Start infrastructure
```mermaid
 docker-compose up -d
 docker ps
```
2) Run services
```mermaid
 Start order-service (port 8080)
 Start payment-service (port 8081)
```
3) Create Order
```mermaid
 curl -X POST http://localhost:8080/orders ^
 -H "Content-Type: application/json" ^
 -d "{\"amount\": 222.22}"
```
4) Verify Order status Updated
```mermaid
 curl http://localhost:8080/orders
```
5) Swagger
```mermaid
 Order Service: http://localhost:8080/swagger-ui/index.html
 Payment Service: http://localhost:8081/swagger-ui/index.html
```
## Reliability Features
* Idempotency: payment-service ignores duplicate order.created events using existsByOrderId
* DLT: failed messages are retried and then routed to order.created.DLT

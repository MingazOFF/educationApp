# Проект "Сервис по управлению задачами" (Simple CRUD REST service for Tasks)

Веб-приложение написано во время обучения в открытой школе для Java-разработчиков "Т1 Цифровой Академии".


## Особенности:
- логгирование методов сервиса и контроллера через Aspect
- использование Kafka для отслеживания изменения статуса у задач
- отправка уведомлений на Email об изменении статуса задачи
- функциональные и интеграционные тесты

## End-Points:

- POST   /tasks       — create a new Task
- GET    /tasks/{id}  — get Task by ID.
- PUT    /tasks/{id}  — update Task.
- DELETE /tasks/{id}  — delete Task by ID.
- GET    /tasks       — get all Tasks.

## Технологии

- Java
- Spring Boot Framework (WEB, Data JPA, Mail, AOP)
- Lombok
- PostgreSQL
- JUnit
- Mockito
- Kafka

## Автор

 Ренат Мингазов - [mingazofff@gmail.com]

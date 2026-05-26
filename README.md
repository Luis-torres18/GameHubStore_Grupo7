```
PROYECTO: Game_Hub_Store - Evaluacion Parcial 2
ASIGNATURA: FULL STACK I_002D/001D
INTEGRANTES: Benjamin Leiva(001D), Emily Pacheco(002D) y Luis Torres(002D).
```
```
1. Descripción del Proyecto
GameHub Store es una tienda gamer online construida con arquitectura de microservicios 
usando Spring Boot. El sistema gestiona el ciclo completo de venta de productos 
tecnológicos y gamer: catálogo, categorías, usuarios, autenticación, inventario, órdenes, 
pagos, despachos, promociones, reseñas y garantías. 

Cada microservicio tiene su propia base de datos, CRUD completo, 
validaciones, manejo de excepciones y logs estructurados.
```
2. ESTRUCTURA DEL PROYECTO
```
GameHub_Store/
├── auth-service/ || Puerto: 8080
├── user-service/ || Puerto: 8081
├── product-service/ || Puerto: 8082
├── category-service/ || Puerto: 8083
├── inventory-service/ || Puerto: 8084
├── order-service/ || Puerto: 8085
├── payment-service/ || Puerto: 8086
├── shipping-service/ || Puerto: 8087
├── promotion-service/ || Puerto: 8088
├── review-service/ || Puerto: 8089
├── warranty-service/ || Puerto: 8090
└──  notification-service/ || Puerto: 8091
```

3. Pasos para Ejecutar

  - Requisitos previos
  - Java 25
  - IntelliJ IDEA (Recomendado)
  - Maven Instalado
  
  1. Clonar el repositorio
  - Para clonar el repositorio requerimos los siguientes dos comandos en el mismo orden:
  - git https://github.com/Luis-torres18/GameHubStore_Grupo7.git
  - cd GameHubStore_Grupo7
  
  2. Ejecutar cada microservicio
  - Desde el IntelliJ, debes abrir cada proyecto y ejecutar la clase principal
    (*Application.Java)
  
  3. Verificar que esten funcionando
  - En el explorador de prefencia que tengas deberas acceder a la consola H2 de cada microservicios
    para verificar que la base de datos este funcionando, en el buscador debes colocar:
    https://localhost:8080/h2-console, deberas ir cambiando el puerto segun el micro servicio que estes
    verificando.

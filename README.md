## MQ Shipment Notification System (Sistema de Notificación de Envíos con MQ)

Este proyecto consta de dos microservicios de Spring Boot que gestionan notificaciones de envíos utilizando IBM MQ. El servicio `mq-shipment-sender` envía solicitudes de envío a una cola, y el servicio `mq-shipment-processor` procesa esas solicitudes, simula el envío de un correo electrónico y envía una respuesta de vuelta a una cola de respuestas.

This project consists of two Spring Boot microservices that handle shipment notifications using IBM MQ. The `mq-shipment-sender` service sends shipment requests to a queue, and the `mq-shipment-processor` service processes those requests, simulates sending an email, and sends a response back to a response queue.

---

## Tabla de Contenidos (Table of Contents)

- [Tecnologías / Technologies](#tecnologías--technologies)
- [Arquitectura / Architecture](#arquitectura--architecture)
- [Prerrequisitos / Prerequisites](#prerrequisitos--prerequisites)
- [Configuración y Ejecución / Setup and Run](#configuración-y-ejecución--setup-and-run)
- [Cómo Funciona / How It Works](#cómo-funciona--how-it-works)

---

## Tecnologías / Technologies

- Java 17
- Spring Boot 3.3.3
- IBM MQ
- Docker & Docker Compose
- Gradle
- JMS
- Jackson (JSON processing)

---

## Arquitectura / Architecture

El proyecto sigue una arquitectura de microservicios, donde cada servicio maneja responsabilidades específicas.

The project follows a microservices architecture, where each service handles specific responsibilities.

- **mq-shipment-sender**: Envía solicitudes de envío a una cola de solicitudes en IBM MQ. / Sends shipment requests to an IBM MQ request queue.
- **mq-shipment-processor**: Escucha solicitudes de envío, las procesa, simula el envío de un correo electrónico, y envía una respuesta a la cola de respuestas. / Listens for shipment requests, processes the request, sends an email notification (simulated), and sends a response to a response queue.

### Estructura de las Colas / Queue Structure
- `DEV.QUEUE.1`: Donde se envían las solicitudes de envío por el servicio `mq-shipment-sender`. / Where shipment requests are sent by the `mq-shipment-sender`.
- `DEV.QUEUE.2`: Donde se envían las respuestas de envío por el servicio `mq-shipment-processor`. / Where shipment responses are sent by the `mq-shipment-processor`.

---

## Prerrequisitos / Prerequisites

Antes de ejecutar el proyecto, necesitas tener instalados los siguientes componentes:

Before running the project, you need to have the following installed:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [IBM MQ](https://developer.ibm.com/tutorials/mq-connect-app-queue-manager-containers/#summary8) (via Docker)
- [Java 17](https://adoptium.net/)
- [Gradle](https://gradle.org/install/)

---

## Configuración y Ejecución / Setup and Run

1. **Clona el repositorio / Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/mq-shipment-notification.git
    cd mq-shipment-notification
    ```

2. **Inicia IBM MQ y ambos servicios usando Docker Compose / Start IBM MQ and both services using Docker Compose**:
   Asegúrate de que Docker esté en ejecución, luego ejecuta / Ensure that Docker is running, and then execute:
    ```bash
    docker-compose up -d --build
    ```

   Esto levantará los siguientes servicios / This will spin up the following services:
    - `ibmmq`: Contenedor de IBM MQ / IBM MQ container
    - `shipment-processor`: Escucha en la cola de solicitudes y procesa las solicitudes de envío / Listens to the request queue and processes shipment requests
    - `shipment-sender`: Envía solicitudes de envío a la cola de solicitudes / Sends shipment requests to the request queue

3. **Detener los contenedores / Stop the containers**:
   Cuando hayas terminado, puedes detener los servicios ejecutando / Once you're done with the services, you can stop them by running:
    ```bash
    docker-compose down
    ```

4. **Reconstruir los servicios / Rebuilding the services**:
   Si haces cambios en el código y necesitas reconstruir los contenedores, ejecuta / If you make changes to the code and need to rebuild the containers, run:
    ```bash
    docker-compose up -d --build
    ```

## Cómo Funciona / How It Works

1. **Enviar una solicitud de envío / Send a shipment request**:
   Puedes usar `Postman` o `curl` para enviar una solicitud de envío al servicio `mq-shipment-sender`.

   You can use `Postman` or `curl` to send a shipment request to the `mq-shipment-sender` service.

   Ejemplo de solicitud / Example request:
   ```bash
   curl -X POST http://localhost:8080/api/shipments \
   -H "Content-Type: application/json" \
   -d '{
     "orderId": "abc123",
     "customerEmail": "customer@example.com",
     "trackingNumber": "TRK987654",
     "shippingDate": "2024-09-14"
   }'

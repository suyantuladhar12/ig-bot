# IG-Bot

An event-driven AI messaging bot built with **Spring Boot**, **Python**, **Redis**, **PostgreSQL**, and **Docker**. The application listens for incoming Discord messages, stores conversation history, delegates AI processing to a separate Python worker through Redis queues, and automatically sends AI-generated replies back to the user.

The architecture is designed to be platform-agnostic, making it easy to extend support to Messenger, Instagram, or other messaging platforms.

## Architecture

```text
Discord/Messenger
        │
        ▼
 Spring Boot Backend
        │
        ├── Save conversation → PostgreSQL
        └── Push job → Redis (incoming_messages)
                        │
                        ▼
                Python AI Worker
                        │
                  Groq LLM API
                        │
        Save reply → PostgreSQL
                        │
        Push reply → Redis (outgoing_messages)
                        │
                        ▼
             Spring Boot Reply Worker
                        │
                        ▼
             Discord/Messenger User
```

## Tech Stack

- Java 21
- Spring Boot
- Python
- Redis
- PostgreSQL
- Docker & Docker Compose
- JDA (Discord API)
- Groq API (OpenAI-compatible)

## Features

- Discord DM listener
- AI-powered replies
- Persistent conversation history
- Redis-based job queues
- Separate AI worker service
- Dockerized multi-service architecture

## Run

```bash
docker compose up --build
```

## Future Plans

- Messenger & Instagram support
- Queue retries
- Database migrations
- Automated tests
- Multi-worker support

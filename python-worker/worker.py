import os
import json
import redis
import psycopg2
import psycopg2.extras
from openai import OpenAI

redis_client = redis.Redis(
    host=os.environ.get("REDIS_HOST", "localhost"),
    port=int(os.environ.get("REDIS_PORT", 6379)),
    decode_responses=True
)

db_conn = psycopg2.connect(
    host=os.environ.get("DB_HOST", "localhost"),
    port=os.environ.get("DB_PORT", 5432),
    dbname=os.environ.get("DB_NAME", "igbot"),
    user=os.environ.get("DB_USER", "igbot"),
    password=os.environ.get("DB_PASSWORD")
)

ai_client = OpenAI(
    api_key=os.environ.get("GROQ_API_KEY"),
    base_url="https://api.groq.com/openai/v1"
)

def get_history(sender_id):
    cursor = db_conn.cursor()
    cursor.execute(
        "SELECT role, text FROM chat_messages WHERE sender_id = %s ORDER BY created_at ASC",
        (sender_id,)
    )
    rows = cursor.fetchall()
    cursor.close()

    messages = [{"role": "system", "content": "You are a helpful assistant."}]
    for role, text in rows:
        messages.append({
            "role": "user" if role == "USER" else "assistant",
            "content": text
        })
    return messages

def get_ai_reply(sender_id):
    messages = get_history(sender_id)
    response = ai_client.chat.completions.create(
    model="llama-3.3-70b-versatile",
    messages=messages
    )
    return response.choices[0].message.content

def save_assistant_reply(sender_id, text):
    cursor = db_conn.cursor()
    cursor.execute(
        "INSERT INTO chat_messages (sender_id, text, role, created_at) VALUES (%s, %s, %s, NOW())",
        (sender_id, text, "ASSISTANT")
    )
    db_conn.commit()
    cursor.close()

print("Worker started, waiting for messages...")

while True:
    result = redis_client.brpop("incoming_messages", timeout=0)
    _, job_json = result
    job = json.loads(job_json)

    sender_id = job["senderId"]
    text = job["text"]

    print(f"Got message from {sender_id}: {text}")

    reply = get_ai_reply(sender_id)
    print(f"AI reply: {reply}")
    save_assistant_reply(sender_id, reply)
    outgoing_job = {
    "senderId": sender_id,
    "reply": reply,
    "platform": job["platform"]
    }

    redis_client.lpush(
        "outgoing_messages",
        json.dumps(outgoing_job)
    )

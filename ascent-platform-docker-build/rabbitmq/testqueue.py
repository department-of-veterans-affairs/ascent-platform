#!/usr/bin/env python
import pika

# ------
# Tests insertion of a queue into rabbit
# https://www.rabbitmq.com/tutorials/tutorial-one-python.html


credentials = pika.PlainCredentials('guest', 'default')

connectionParameters = pika.connection.ConnectionParameters('localhost','15672', '/', credentials)
connection = pika.BlockingConnection(connectionParameters)
channel = connection.channel()

channel.queue_declare(queue='hello')


channel.basic_publish(exchange='',
                      routing_key='hello',
                      body='Hello World!')
print(" [x] Sent 'Hello World!'")

connection.close()



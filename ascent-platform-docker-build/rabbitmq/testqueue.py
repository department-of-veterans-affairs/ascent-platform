#!/usr/bin/env python
import pika

credentials = pika.PlainCredentials('guest', 'default')


connectionParameters = pika.connection.ConnectionParameters('ascentplatformdockerbuild_lb_1', 5672, '/', credentials)
connection = pika.BlockingConnection(connectionParameters)
channel = connection.channel()

channel.queue_declare(queue='hello2')


channel.basic_publish(exchange='',
                      routing_key='hello',
                      body='Hello World!')
print(" [x] Sent 'Hello World!'")

connection.close()



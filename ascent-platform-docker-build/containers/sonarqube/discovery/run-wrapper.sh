#!/bin/bash

echo "Finding my ip..."

ip=`hostname -i | awk '{print $1}'`
echo "ip is $ip"

args="-name etcd0 -advertise-client-urls http://$ip:2379,http://$ip:4001 -listen-client-urls http://0.0.0.0:2379,http://0.0.0.0:4001 -initial-advertise-peer-urls http://$ip:2380 -listen-peer-urls http://0.0.0.0:2380 -initial-cluster-token etcd-cluster-1 -initial-cluster etcd0=http://$ip:2380 -initial-cluster-state new"

echo "Running..."
/usr/local/bin/etcd $args

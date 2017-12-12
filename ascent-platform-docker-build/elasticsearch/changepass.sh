#!/bin/bash

pass=$ES_PASSWORD
echo $pass | bin/elasticsearch-keystore add --stdin "bootstrap.password"

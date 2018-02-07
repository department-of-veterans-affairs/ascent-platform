#!/bin/bash

# run scripts
/docker/install-java.sh
/docker/install-tools.sh
/docker/install-jenkins.sh
/docker/configure-jenkins.sh

# block forever
tail -f /dev/null

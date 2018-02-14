#!/bin/bash
echo "---- POLLING ALL SERVICES UNTIL THEY'RE UP"
/usr/share/configure/poll-services.sh
tail -f /var/null

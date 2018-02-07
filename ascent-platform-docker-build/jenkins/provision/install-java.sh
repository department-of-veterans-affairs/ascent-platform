#! /bin/bash

# Install Java Runtime
curl -L -H "Cookie: oraclelicense=accept-securebackup-cookie" -o jdk1.8.0_144.tar.gz http://download.oracle.com/otn-pub/java/jdk/8u144-b01/090f390dda5b47b9b721c7dfaa008135/server-jre-8u144-linux-x64.tar.gz
tar -xzf jdk1.8.0_144.tar.gz
rm jdk1.8.0_144.tar.gz
sudo mkdir -p /usr/lib/jvm
sudo mv jdk1.8.0_144 /usr/lib/jvm/jdk1.8.0_144
sudo chown -R root:root /usr/lib/jvm/jdk1.8.0_144

sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/jdk1.8.0_144/bin/java 1200

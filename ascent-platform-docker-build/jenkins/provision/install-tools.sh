# Update
sudo yum -y update

# Git
sudo yum install -y git unzip

# Maven

# Docker
# See https://docs.docker.com/engine/installation/linux/docker-ce/centos/#install-using-the-repository

# Packer
curl -L -o packer.zip https://releases.hashicorp.com/packer/1.0.4/packer_1.0.4_linux_amd64.zip; \
    unzip packer.zip; \
    sudo mv packer /usr/local/bin/packer; \
    sudo chmod +x /usr/local/bin/packer; \
    rm -f packer.zip

# Consul Template
curl -L -o consul-template_0.19.0_linux_amd64.tgz https://releases.hashicorp.com/consul-template/0.19.0/consul-template_0.19.0_linux_amd64.tgz; \
    tar -xzf consul-template_0.19.0_linux_amd64.tgz; \
    sudo mv consul-template /usr/local/bin/consul-template; \
    sudo chmod +x /usr/local/bin/consul-template; \
    rm -f consul-template_0.19.0_linux_amd64.tgz

# Env Consul
curl -L -o envconsul_0.7.1_linux_amd64.tgz https://releases.hashicorp.com/envconsul/0.7.1/envconsul_0.7.1_linux_amd64.tgz; \
    tar -xzf envconsul_0.7.1_linux_amd64.tgz; \
    sudo mv envconsul /usr/local/bin/envconsul; \
    sudo chmod +x /usr/local/bin/envconsul; \
    rm -f envconsul_0.7.1_linux_amd64.tgz


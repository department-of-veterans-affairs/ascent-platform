# Install the Jenkins RPM repo
sudo curl -L -o /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key

# Install jenkins
sudo yum install -y jenkins
sudo chkconfig jenkins on
sudo service jenkins start


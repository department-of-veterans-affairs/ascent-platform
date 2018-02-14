FROM sonarqube:6.5

# Install unzip for unzipping the vault package
RUN apt-get install unzip

# Install envconsul for retreiving secrets into ENV variables available only to the executed process
RUN curl -sL -o envconsul_0.7.1_linux_amd64.tgz https://releases.hashicorp.com/envconsul/0.7.1/envconsul_0.7.1_linux_amd64.tgz; \
    tar -xzf envconsul_0.7.1_linux_amd64.tgz; \
    mv envconsul /usr/local/bin/envconsul; \
    chmod +x /usr/local/bin/envconsul; \
    rm -f envconsul_0.7.1_linux_amd64.tgz

ADD qualityprofile/java-ascent-32413.xml /qualityprofile/

ADD start_with_profile.sh /opt/sonarqube/start_with_profile.sh

ADD template /opt/sonarqube/template

ADD provision /opt/sonarqube/provision
RUN chmod 755 /opt/sonarqube/provision/*

VOLUME /qualityprofile

ENTRYPOINT ["/opt/sonarqube/run-wrapper.sh"]

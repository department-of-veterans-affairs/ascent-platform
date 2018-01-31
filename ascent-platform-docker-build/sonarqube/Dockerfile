FROM sonarqube:6.5

ADD qualityprofile/java-ascent-32413.xml /qualityprofile/

ADD start_with_profile.sh /opt/sonarqube/start_with_profile.sh

ADD install_github_plugin.sh /opt/sonarqube/install_github_plugin.sh
RUN chmod 755 /opt/sonarqube/install_github_plugin.sh


VOLUME /qualityprofile

ENTRYPOINT ["/opt/sonarqube/start_with_profile.sh"]

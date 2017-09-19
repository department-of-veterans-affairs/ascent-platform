FROM sonarqube:6.5

ADD qualityprofile/java-ascent-32413.xml /qualityprofile/

ADD start_with_profile.sh /opt/sonarqube/start_with_profile.sh

VOLUME /qualityprofile

ENTRYPOINT ["/opt/sonarqube/start_with_profile.sh"]

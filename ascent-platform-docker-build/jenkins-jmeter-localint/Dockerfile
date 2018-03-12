
FROM jenkins/jenkins:2.89.3

MAINTAINER Suchitha

RUN /usr/local/bin/install-plugins.sh performance
COPY jenkins-home/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

ENV JENKINS_REF /usr/share/jenkins/ref

ENV JAVA_OPTS -Dorg.eclipse.jetty.server.Request.maxFormContentSize=100000000 \
 			  -Dorg.apache.commons.jelly.tags.fmt.timeZone=America/Los_Angeles \
 			  -Dhudson.diyChunking=false \
 			  -Djenkins.install.runSetupWizard=false

# copy scripts and ressource files
COPY jenkins-home/*.* $JENKINS_REF/
COPY jenkins-home/userContent $JENKINS_REF/userContent
COPY jenkins-home/jobs $JENKINS_REF/jobs/
COPY jenkins-home/init.groovy.d $JENKINS_REF/init.groovy.d/

USER root


RUN apt-get -m update && apt-get install -y wget  zip

ENV		JMETER_VERSION	4.0
ENV		JMETER_HOME	/opt/jmeter/apache-jmeter-4.0
ENV		JMETER_DOWNLOAD_URL  https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-${JMETER_VERSION}.tgz

# download and extract jmeter 

RUN		mkdir -p ${JMETER_HOME}

RUN 	curl -L --silent ${JMETER_DOWNLOAD_URL} | tar -xz --strip=1 -C ${JMETER_HOME} && \
		curl -L --silent http://jmeter-plugins.org/downloads/file/JMeterPlugins-Standard-1.2.1.zip -o /tmp/jmeter-plugins-standard.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-ggl-2.0.zip -o /tmp/jpgc-ggl-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-graphs-vs-2.0.zip -o /tmp/jpgc-graphs-vs-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-graphs-additional-2.0.zip -o /tmp/jpgc-graphs-additional-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-graphs-basic-2.0.zip -o /tmp/jpgc-graphs-basic-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-graphs-composite-2.0.zip -o /tmp/jpgc-graphs-composite-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-graphs-dist-2.0.zip -o /tmp/jpgc-graphs-dist-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-cmd-2.0.zip -o /tmp/jpgc-cmd-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-filterresults-2.0.zip -o /tmp/jpgc-filterresults-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-mergeresults-2.1.zip -o /tmp/jpgc-mergeresults-2.1.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-perfmon-2.0.zip -o /tmp/jpgc-perfmon-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-sense-2.0.zip -o /tmp/jpgc-sense-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-functions-2.0.zip -o /tmp/jpgc-functions-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-plancheck-2.0.zip -o /tmp/jpgc-plancheck-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-synthesis-2.0.zip -o /tmp/jpgc-synthesis-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-fifo-0.2.zip -o /tmp/jpgc-fifo-0.2.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-ffw-2.0.zip -o /tmp/jpgc-ffw-2.0.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-dummy-0.2.zip -o /tmp/jpgc-dummy-0.2.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-tst-2.1.zip -o /tmp/jpgc-tst-2.1.zip && \
		curl -L --silent https://jmeter-plugins.org/files/packages/jpgc-casutg-2.1.zip -o /tmp/jpgc-casutg-2.1.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0 /tmp/jmeter-plugins-standard.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0 /tmp/jpgc-ggl-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0 /tmp/jpgc-graphs-vs-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0 /tmp/jpgc-graphs-additional-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0 /tmp/jpgc-graphs-basic-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-graphs-composite-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0 /tmp/jpgc-graphs-dist-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-cmd-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0 /tmp/jpgc-filterresults-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-mergeresults-2.1.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-perfmon-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-sense-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-3.2  /tmp/jpgc-functions-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-plancheck-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-synthesis-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-fifo-0.2.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-ffw-2.0.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-dummy-0.2.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-tst-2.1.zip && \
		unzip -o -d /opt/jmeter/apache-jmeter-4.0  /tmp/jpgc-casutg-2.1.zip && \
		rm /tmp/jmeter-plugins-standard.zip

RUN chmod +x $JMETER_HOME
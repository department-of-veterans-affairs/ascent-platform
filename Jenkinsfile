pipeline {
  agent any
  
  stages {
    stage('Build Ascent Parent POM') {
      tools {
        maven 'Maven'
      }
      steps {
        dir('ascent-platform-parent') {
          sh 'mvn -Dmaven.wagon.http.ssl.insecure=true clean install'
        }
      }
      /*
      post {
        always {
          junit '**/target/surefire-reports/**/*.xml'
        }
      }
      */
    }
  }
}
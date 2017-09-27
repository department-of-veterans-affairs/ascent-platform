pipeline {
  agent any
  
  stages {
    stage('Maven Build') {
      tools {
        maven 'Maven'
      }
      steps {
        sh 'mvn -Dmaven.wagon.http.ssl.insecure=true clean install'
      }
      post {
        always {
          junit 'target/surefire-reports/**/*.xml'
        }
      }
    }
  }
}
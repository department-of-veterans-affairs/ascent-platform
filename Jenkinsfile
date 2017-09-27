pipeline {
  agent any
  tools {
    maven 'Maven'
  }
  stages {
    stage('Maven Build') {
      steps {
        sh 'mvn clean install'
      }
      post {
        always {
          junit 'target/surefire-reports/**/*.xml'
        }
      }
    }
  }
}
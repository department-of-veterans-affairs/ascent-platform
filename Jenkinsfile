pipeline {
  agent any
  
  stages {
    stage('Maven Build') {
      tools {
        maven 'Maven'
      }
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
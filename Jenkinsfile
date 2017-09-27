pipeline {
  agent any
  stages {
    stage('Checkout') {
      steps {
        git(url: 'https://github.com/department-of-veterans-affairs/ascent-platform', credentialsId: 'github', poll: true)
      }
    }
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
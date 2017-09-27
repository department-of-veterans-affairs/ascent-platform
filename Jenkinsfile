pipeline {
  agent any
  
  stages {
    stage('Build Ascent Parent POM') {
      tools {
        maven 'Maven'
      }
      steps {
        dir('ascent-platform-parent') {
          withCredentials([usernamePasswordMultiBinding(credentialsId: 'nexus', usernameVariable: 'DEPLOY_USER', passwordVariable: 'DEPLOY_PASSWORD')]) {
            sh 'mvn -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -s ../settings.xml clean deploy'
          }
        }
      }
      // post {
      //   always {
      //     junit '**/target/surefire-reports/**/*.xml'
      //   }
      // }
    }
  }
}
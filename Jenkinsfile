pipeline {
  agent any
  
  stages {
    stage('Build Ascent Parent POM') {
      tools {
        maven 'Maven'
      }
      steps {
        dir('ascent-platform-parent') {
          sh 'mvn -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true clean deploy'
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
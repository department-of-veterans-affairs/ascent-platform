pipeline {
  agent any
  
  stages {
    stage('Build Ascent Parent POM') {
      tools {
        maven 'Maven'
      }
      steps {
        dir('ascent-platform-parent') {
          withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'DEPLOY_USER', passwordVariable: 'DEPLOY_PASSWORD')]) {
            sh 'mvn -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -s ../settings.xml clean deploy'
          }
        }
      }
    }

    stage('Ascent Base') {
      tools {
        docker 'latest'
      }
      steps {
        dir('ascent-platform-docker-build/ascent-base') {
          sh 'docker build -t ascent/ascent-base .'
        }
      }
      // post {
      //   success {
      //     withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASSWORD')]) {
      //       sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USER --password-stdin'
      //       sh 'docker push ascent/ascent-base'
      //     }
      //   }
      // }
    }


  }
}
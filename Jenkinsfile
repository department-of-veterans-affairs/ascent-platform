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
      steps {
        dir('ascent-platform-docker-build/ascent-base') {
          script {
            docker.withServer('tcp://ip-10-247-80-51.us-gov-west-1.compute.internal:2375') {
              docker.withRegistry('https://index.docker.io/v1/', 'dockerhub') {
                def image = docker.build('ascent/ascent-base:${BRANCH_NAME}')
              }
            }
          }
        }
      }
      post {
        success {
          script {
            docker.withServer('tcp://ip-10-247-80-51.us-gov-west-1.compute.internal:2375') {
              docker.withRegistry('https://index.docker.io/v1/', 'dockerhub') {
                def image = docker.build('ascent/ascent-base:${BRANCH_NAME}')
                image.push()
                if (env.BRANCH_NAME == 'development') {
                  image.push('latest')
                }
              }
            }
          }
        }
      }
    }


  }
}
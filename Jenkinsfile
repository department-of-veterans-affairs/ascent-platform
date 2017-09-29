@Library('ascent') _

pipeline {
    agent any

    triggers {
        //Check SCM every 5 minutes
        pollSCM('*/5 * * * *')
    }

    mavenBuild {
        directory = 'ascent-platform-parent'
    }
}
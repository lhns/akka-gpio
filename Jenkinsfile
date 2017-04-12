pipeline {
  agent any
  stages {
    stage('Install SBT') {
      steps {
        tool 'default-sbt'
      }
    }
    stage('Compile') {
      steps {
        sh 'sbt compile'
      }
    }
  }
}
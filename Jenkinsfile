pipeline {
  agent any
  stages {
    stage('Install SBT') {
      steps {
        tool 'default-sbt'
        sh 'sbt compile'
      }
    }
  }
}
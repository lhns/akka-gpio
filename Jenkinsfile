pipeline {
  agent any
  stages {
    stage('Compile') {
      steps {
        tool(name: 'default-sbt', type: 'compile')
      }
    }
  }
}
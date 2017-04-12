pipeline {
  agent {
    docker {
      image '--name sbt lolhens/sbt:latest'
    }
    
  }
  stages {
    stage('Compile') {
      steps {
        sh '#sbt compile'
      }
    }
  }
}
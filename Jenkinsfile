pipeline {
  agent {
    docker {
      image 'lolhens/sbt:latest'
    }
    
  }
  stages {
    stage('Compile') {
      steps {
        sh 'sbt compile'
      }
    }
  }
}
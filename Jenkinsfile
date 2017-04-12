pipeline {
  agent {
    docker {
      image 'lolhens/sbt:latest'
      args '-u root --name sbt'
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
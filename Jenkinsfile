pipeline {
  agent {
    docker {
      image 'lolhens/sbt:latest'
      args '-u root'
    }
    
  }
  stages {
    stage('Build') {
      steps {
        sh 'sbt package'
        archiveArtifacts(artifacts: 'target/scala-*/*.jar', fingerprint: true, onlyIfSuccessful: true)
      }
    }
  }
}
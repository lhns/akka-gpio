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
        sh 'sbt package'
      }
    }
    stage('Archive Artifacts') {
      steps {
        archiveArtifacts(artifacts: 'target/scala-*/*.jar', onlyIfSuccessful: true, fingerprint: true)
      }
    }
  }
}
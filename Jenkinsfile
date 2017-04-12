pipeline {
  agent {
    docker {
      image 'lolhens/sbt:latest'
      args '-u root --name sbt'
    }
    
  }
  stages {
    stage('Package') {
      steps {
        sh 'sbt package'
      }
    }
    stage('Collect Artifacts') {
      steps {
        archiveArtifacts(artifacts: 'target/scala-*/*.jar', onlyIfSuccessful: true, fingerprint: true)
      }
    }
  }
}
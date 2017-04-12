pipeline {
  agent {
    docker {
      image 'lolhens/sbt:latest'
      args '-u root --name sbt'
    }
    
  }
  stages {
    stage('Build') {
      steps {
        sh 'sbt package'
        archiveArtifacts(artifacts: 'target/scala-*/*.jar', fingerprint: true, onlyIfSuccessful: true)
      }
    }
    stage('Deploy') {
      steps {
        script {
          
        }
        
      }
    }
  }
}
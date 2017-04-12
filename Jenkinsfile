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
        sh 'sbt publishLocal'
        archiveArtifacts(artifacts: 'target1/scala-*/*', fingerprint: true, onlyIfSuccessful: true)
      }
    }
    stage('Deploy') {
      steps {
        script {
          def server = Artifactory.server 'artifactory'
          
          def uploadSpec = """{
            "files": [
              {
                "pattern": "*",
                "target": "test/asdf/"
              }
            ]
          }"""
          
          server.upload(uploadSpec)
        }
        
      }
    }
  }
}
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
      }
    }
    stage('Deploy') {
      steps {
        script {
          def server = Artifactory.server 'artifactory'
          
          def uploadSpec = """{
            "files": [
              {
                "pattern": "target/releases/default",
                "target": "test/org/lolhens"
              }
            ]
          }"""
          
          server.upload(uploadSpec)
        }
        
      }
    }
  }
}
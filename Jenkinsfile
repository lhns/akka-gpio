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
        sh 'sbt publish'
        archiveArtifacts(artifacts: 'target/releases/*/*/*/*', onlyIfSuccessful: true)
        sh '''ls target/releases/*/*/*/*
echo target/releases/akka-gpio/akka-gpio_2.12/1.0.0'''
      }
    }
    stage('Deploy') {
      steps {
        script {
          def server = Artifactory.server 'artifactory'
          
          def uploadSpec = """{
            "files": [
              {
                "pattern": "target/releases/*/*/*/*",
                "target": "test/org/lolhens/"
              }
            ]
          }"""
          
          server.upload(uploadSpec)
        }
        
      }
    }
  }
}
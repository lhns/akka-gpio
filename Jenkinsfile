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
        sh '''echo '
[repositories]
  local
  Artifactory: http://lolhens.no-ip.org/artifactory/maven-public/
'>repositories

#sbt -Dsbt.boot.properties="sbt.boot.properties" publish
sbt -Dsbt.repository.config=repositories publish
'''
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
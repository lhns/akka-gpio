pipeline {
  agent {
    docker {
      image 'lolhens/sbt:latest'
      args '-u root'
    }
    
  }
  stages {
    stage('Clean') {
      steps {
        sh '''sbt -no-colors clean
'''
      }
    }
    stage('Build') {
      steps {
        sh '''echo '[repositories]
  local
  artifactory-ivy: http://lolhens.no-ip.org/artifactory/maven-public/, [organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext], bootOnly
  artifactory: http://lolhens.no-ip.org/artifactory/maven-public/
'>repositories
'''
        sh '''sbt -no-colors -Dsbt.repository.config=repositories publish
'''
      }
    }
    stage('Deploy') {
      steps {
        parallel(
          "Deploy": {
            script {
              def server = Artifactory.server 'artifactory'
              
              def repository = "local-releases"
              
              def uploadSpec = """{
                "files": [
                  {
                    "pattern": "target/releases/(*)/*.jar",
                    "target": "${repository}/{1}/"
                  },
                  {
                    "pattern": "target/releases/(*)/*.pom",
                    "target": "${repository}/{1}/"
                  }
                ]
              }"""
              
              server.upload(uploadSpec)
            }
            
            
          },
          "Artifacts": {
            archiveArtifacts(artifacts: 'target/releases/**', onlyIfSuccessful: true)
            
          }
        )
      }
    }
  }
}
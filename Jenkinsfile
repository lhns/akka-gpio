pipeline {
  agent {
    docker {
      image 'lolhens/sbt:latest'
      args '-u root'
    }
    
  }
  stages {
    stage('Setup') {
      steps {
        sh '''echo '[repositories]
  local
  artifactory-ivy: http://lolhens.no-ip.org/artifactory/maven-public/, [organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext], bootOnly
  artifactory: http://lolhens.no-ip.org/artifactory/maven-public/
'>repositories
'''
      }
    }
    stage('Clean') {
      steps {
        sh 'sbt clean'
      }
    }
    stage('Build') {
      steps {
        sh '''sbt publish
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
              
              def buildInfo = server.upload(uploadSpec)
              
              server.publishBuildInfo(buildInfo)
            }
            
            
          },
          "Artifacts": {
            archiveArtifacts(artifacts: 'target/releases/**', onlyIfSuccessful: true)
            
          }
        )
      }
    }
  }
  environment {
    SBT_OPTS = '-Dsbt.log.noformat=true -Dsbt.repository.config=repositories'
  }
}
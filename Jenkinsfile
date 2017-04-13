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
        sh '''echo '
[repositories]
  local
  artifactory-ivy: http://lolhens.no-ip.org/artifactory/maven-public/, [organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext], bootOnly
  artifactory: http://lolhens.no-ip.org/artifactory/maven-public/
'>repositories

printenv

sbt clean
#sbt -Dsbt.boot.properties="sbt.boot.properties" publish
sbt -Dsbt.repository.config=repositories publish
'''
}}
    stage('Build') {
      steps {
        sh '''echo '
[repositories]
  local
  artifactory-ivy: http://lolhens.no-ip.org/artifactory/maven-public/, [organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext], bootOnly
  artifactory: http://lolhens.no-ip.org/artifactory/maven-public/
'>repositories

printenv

sbt clean
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
                "pattern": "target/releases/(*)/*.jar",
                "target": "test/{1}/"
              },
              {
                "pattern": "target/releases/(*)/*.pom",
                "target": "test/{1}/"
              }
            ]
          }"""
          
          server.upload(uploadSpec)
        }
        
      }
    }
  }
}
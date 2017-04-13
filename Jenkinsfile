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
        sh '''echo [scala]>>sbt.boot.properties
echo   version: ${sbt.scala.version-auto}>>sbt.boot.properties

echo [app]>>sbt.boot.properties
echo   org: ${sbt.organization-org.scala-sbt}>>sbt.boot.properties
echo   name: sbt>>sbt.boot.properties
echo   version: ${sbt.version-read(sbt.version)[0.13.5]}>>sbt.boot.properties
echo   class: ${sbt.main.class-sbt.xMain}>>sbt.boot.properties
echo   components: xsbti,extra>>sbt.boot.properties
echo   cross-versioned: ${sbt.cross.versioned-false}>>sbt.boot.properties

echo [repositories]>>sbt.boot.properties
echo   local>>sbt.boot.properties
echo   Artifactory: http://lolhens.no-ip.org/artifactory/maven-public/>>sbt.boot.properties

echo [boot]>>sbt.boot.properties
echo   directory: ${sbt.boot.directory-${sbt.global.base-${user.home}/.sbt}/boot/}>>sbt.boot.properties

echo [ivy]>>sbt.boot.properties
echo   ivy-home: ${sbt.ivy.home-${user.home}/.ivy2/}>>sbt.boot.properties
echo   checksums: ${sbt.checksums-sha1,md5}>>sbt.boot.properties
echo   override-build-repos: ${sbt.override.build.repos-false}>>sbt.boot.properties
echo   repository-config: ${sbt.repository.config-${sbt.global.base-${user.home}/.sbt}/repositories}>>sbt.boot.properties

sbt -Dsbt.boot.properties="sbt.boot.properties" publish'''
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
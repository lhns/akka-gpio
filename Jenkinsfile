pipeline {
  agent {
    docker {
      image 'lolhens/sbt:latest'
      args '-u root'
    }
    
  }
  stages {
    stage('Compile') {
      steps {
        sh '''echo $HOME
echo $sbt_version
echo $sbt_home
whoami

#sbt'''
      }
    }
  }
}
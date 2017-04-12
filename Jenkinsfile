pipeline {
  agent {
    docker {
      image 'lolhens/sbt:latest'
    }
    
  }
  stages {
    stage('Compile') {
      steps {
        sh '''echo $HOME
echo $sbt_version
echo $sbt_home

#sbt'''
      }
    }
  }
}
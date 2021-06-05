
pipeline {
    agent {
        label 'master'
    }

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "M3"
    }

    stages {
        stage('Build') {
            steps {
                git changelog: true, credentialsId: 'atonutcsgit', poll: false , url: 'https://github.com/AtonuGanguly/batch10.git'
                
                sh "mvn clean package";
                
                
            }
        }
        
        
    }
    post { 
        always { 
           cleanWs()
           deleteDir()
        }
    }
               
    
}

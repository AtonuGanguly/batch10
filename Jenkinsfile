void sendMail(String SUBJECT,String RECEIPIENTS,String MESSAGE){
	
	/*emailext body: "$MESSAGE",
			subject: "${SUBJECT}",
			to: "${RECEIPIENTS}";*/

	sh "mailx -s \"${SUBJECT}\" ${RECEIPIENTS} <<< \"${MESSAGE}\" ";
	
}
void sendNotify(){
	String body = "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL}";
	String subject= "Jenkins Build ${currentBuild.currentResult}: Job ${env.JOB_NAME}";
	String to= "${params.TOMAIL}";
	//sending mail after completion
	try {
		sendMail(subject,to,body);
	} catch (error) {
		echo "Mail Notify failed";
	}	
}

pipeline {
    agent {
        label 'master'
    }

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "M3"
    }
    options {
		buildDiscarder(logRotator(numToKeepStr: '5'));
		timestamps();
		timeout(time: 10, unit: 'MINUTES');
		copyArtifactPermission(env.JOB_NAME);
	}
	
	parameters {
		booleanParam(name: 'SONAR_USE', defaultValue: false,description: 'Sonar Qube Check!');
		string(name: 'TOMAIL', defaultValue: 'atonu.ganguly@tcs.com',description: 'Default Recipient');
	}

    stages {
        stage('Build') {
            steps {
                git changelog: true, credentialsId: 'atonutcsgit', poll: false , url: 'https://github.com/AtonuGanguly/batch10.git'
                script {
					sh 'mvn clean compile test package';
					
					if(params.SONAR_USE){	
						withSonarQubeEnv('sonar') {
							sh 'mvn sonar:sonar -Dsonar.login=3758e748f2851bdbda553bf49188ec7d3ba75208'
						}
					}
				}
                junit '**/test-results/test/*.xml';
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'coverage', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: 'Coverage Report']);
                
                
            }
        }
        stage('Docker') {
            agent {
                label 'docker-slave'
            }
            steps {
				script{
					docker.withRegistry('https://hub.docker.com', 'Dockerhub') {
						def dockerImage = docker.build("atonuhere/bootcamp10:latest");
						dockerImage.push();
					}
				}	
            }
        }

		stage('Ansible') {
            agent {
                label 'docker-slave'
            }
			steps {
				script{
					// Ensure the Ansible image is ready to go.
					ansiblePlaybook credentialsId: 'ansible_aws', inventory: 'inventories/a/hosts', playbook: 'bootcamp10.yml'
				}	
            }
        }
				
        
        
    }
    post { 
        always { 
            sendNotify(); /* Send Mail Notification */
            cleanWs();
            deleteDir();
        }
    }
               
    
}

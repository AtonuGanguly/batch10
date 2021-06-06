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
	environment {
		imagename = 'atonuhere/bootcamp10'
		registryCredential = 'Dockerhub'
		dockerImage = ''
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
				withCredentials([usernamePassword(credentialsId: 'Dockerhub', passwordVariable: 'DOCKER_REGISTRY_PWD', usernameVariable: 'DOCKER_REGISTRY_USER')]) {

					script{
						sh 'docker login -u="$DOCKER_REGISTRY_USER" -p="$DOCKER_REGISTRY_PWD"';
						sh "docker build -t ${imagename}:latest";
						sh "docker push ${imagename}:latest";
						
					}
				}				
            }
        }

		stage('Ansible') {
            agent {
                label 'docker-slave'
            }
			steps {
				withCredentials([usernamePassword(credentialsId: 'ansible_aws', passwordVariable: 'ANSIBLE_PWD', usernameVariable: 'ANSIBLE_USER')]) {
					script{
						sh 'cp ansible/* .';
						sh 'ansible-playbook vpc-provision.yml -i hosts -vv';
						sh 'ansible-playbook provision.yml -i hosts -vv';
						sh 'ansible-playbook bootcamp10.yml -vv --private-key';
					}
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

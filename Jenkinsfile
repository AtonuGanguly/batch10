void sendMail(String SUBJECT,String RECEIPIENTS,String MESSAGE){
	
	/*emailext body: "$MESSAGE",
			subject: "${SUBJECT}",
			to: "${RECEIPIENTS}";*/

	sh "mailx -s \"${SUBJECT}\"  ${RECEIPIENTS}  < \"${MESSAGE}\" ";
	
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
        label 'docker-slave'
    }

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "M3"
    }
	
    options {
		buildDiscarder(logRotator(numToKeepStr: '5'));
		timestamps();
		timeout(time: 10, unit: 'MINUTES');
	}
	environment {
		imagename = 'atonuhere/bootcamp10'
	}
	
	parameters {
		booleanParam(name: 'SONAR_USE', defaultValue: false,description: 'Sonar Qube Check!');
		string(name: 'TOMAIL', defaultValue: 'atonu.ganguly@tcs.com',description: 'Default Recipient');
	}

    stages {
        stage('Build') {
            steps {
                git branch: 'master', credentialsId: 'atonutcsgit', url: 'https://github.com/AtonuGanguly/batch10'
                script {
					sh 'mvn clean compile package';
					
					if(params.SONAR_USE){	
						withSonarQubeEnv('sonar') {
						    sh 'mvn test -Dmaven.test.failure.ignore=true';
						    sh 'ls -lR target';
							sh 'mvn sonar:sonar -Dsonar.login=9f27722ccb323b764046a51f1c6b0723a3f6d701';
						}
					}
				}
				
                junit 'target/surefire-reports/*.xml';
                publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/surefire-reports', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: ''])
                
                
            }
        }
        stage('Docker') {
            
            steps {
				withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'pass', usernameVariable: 'user')]) {
                    script{
						sh 'ls -lR';
						sh "sudo docker login --username=${user} --password=${pass}";
						sh "sudo docker build -t ${imagename}:latest .";
						sh "sudo docker push ${imagename}:latest";
						//sh "sudo docker run -id ${imagename}:latest";
						
					}
				}				
            }
        }
		stage('Ansible') {
            
			steps {
				withCredentials([usernamePassword(credentialsId: 'ansible_aws', passwordVariable: 'ANSIBLE_PWD', usernameVariable: 'ANSIBLE_USER')]) {
					script{
						dir('ansible'){
							sh 'ansible-playbook vpc-provision.yml -i hosts -vv';
							sh 'ansible-playbook provision.yml -i hosts -vv';
						}
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

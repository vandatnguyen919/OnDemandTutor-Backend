pipeline {
    agent any
    environment {
        JIRA_CREDENTIALS = credentials('JIRA-API-TOKEN')
    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    // Checkout the code
                    checkout scm
                    // Extract Jira issue key from the latest commit message
                    def commitMessage = bat(script: 'git log -1 --pretty=%%B', returnStdout: true).trim()
                    def jiraIssueKey = commitMessage.find(/NNS-\d+/) // Adjust the regex to match your Jira issue key pattern and replace PROJECT to PROJECT KEY
                    if (!jiraIssueKey) {
                        error "Jira issue key not found in the commit message."
                    }
                    env.JIRA_ISSUE_KEY = jiraIssueKey
                }
            }
        }
        stage('Build') {
            steps {
                bat 'mvn clean test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Update Jira') {
            steps {
                script {
                    def testResults = junit 'target/surefire-reports/*.xml'
                    def jiraIssueKey = env.JIRA_ISSUE_KEY
                    def jiraAuth = "Basic " + "${JIRA_CREDENTIALS}".bytes.encodeBase64().toString()
                    def status = testResults.failCount == 0 ? "Pass" : "Fail"
                    def attachment = "target/surefire-reports/TEST-com.mytutor.controllers.TestAuthController.xml"

                    echo "Test Results: ${testResults}"
                    echo "JIRA Issue Key: ${jiraIssueKey}"
                    echo "JIRA Auth: ${jiraAuth}"
                    echo "Status: ${status}"
                    echo "Attachment: ${attachment}"
                    // Update the custom field "Testcase Result" on Jira
//                     httpRequest(
//                         url: "https://nguyenvandat.atlassian.net/rest/api/2/issue/${jiraIssueKey}/transitions",
//                         httpMode: 'POST',
//                         customHeaders: [
//                             [name: 'Authorization', value: jiraAuth],
//                             [name: 'Content-Type', value: 'application/json']
//                         ],
//                         requestBody: """
//                         {
//                             "fields": {
//                                 "customfield_12345": "${status}" // Replace 'customfield_12345' with the ID of the 'Testcase Result' field
//                             }
//                         }
//                         """
//                     )
                    //Attach test result file to Jira issue
                     bat """
                        curl -X POST \
                        -H "Authorization: ${jiraAuth}" \
                        -H "X-Atlassian-Token: no-check" \
                        -F "file=@${attachment}" \
                        https://tienhtse184130.atlassian.net/rest/api/2/issue/${jiraIssueKey}/attachments
                    """
                }
            }
        }
    }
}
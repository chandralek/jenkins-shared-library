import jenkins.*

def call(){
  pipeline{
    agent {
      label 'SLAVE'
    }

    environment {
      AWS         = credentials('AWS')
      RDS         = credentials('RDS-MYSQL-PASS-DEV')
      TF_VAR_GIT  = credentials('GitUserPass')
      TF_VAR_R    = credentials('RabbitMq')
      TF_VAR_SSH  = credentials('SSH_ROOT')
      LICENSE_KEY = credentials('LICENSE_KEY')
      API_KEY     = credentials('API_KEY')
    }


    stages{
      stage('Terramform init')
              {
                steps{
                  sh '''
                  export TF_VAR_DBUSER=${RDS_USR}
                  export TF_VAR_DBPASS=${RDS_PSW}
                  export AWS_ACCESS_KEY_ID="${AWS_USR}"
                  export AWS_SECRET_ACCESS_KEY="${AWS_PSW}"
                  export AWS_DEFAULT_REGION="us-west-2"
                  export TF_VAR_LICENSE_KEY=$LICENSE_KEY
                  export TF_VAR_API_KEY=$API_KEY
                  terraform get -update
                  terraform init
              '''
                }
              }
      stage('Terramform apply')
              {
                when{
                  expression{
                    params.ACTION == 'APPLY'
                  }
                }
                steps{
                  sh '''
                  export TF_VAR_DBUSER=${RDS_USR}
                  export TF_VAR_DBPASS=${RDS_PSW}
                  export AWS_ACCESS_KEY_ID="${AWS_USR}"
                  export AWS_SECRET_ACCESS_KEY="${AWS_PSW}"
                  export AWS_DEFAULT_REGION="us-west-2"
                   export TF_VAR_LICENSE_KEY=$LICENSE_KEY
                  export TF_VAR_API_KEY=$API_KEY
                  terraform apply -auto-approve
              '''
                }
              }
      stage('Terramform destroy')
              {
                when{
                  expression{
                    params.ACTION == 'DESTROY'
                  }
                }
                steps{
                  sh '''
                  export TF_VAR_DBUSER=${RDS_USR}
                  export TF_VAR_DBPASS=${RDS_PSW}
                  export AWS_ACCESS_KEY_ID="${AWS_USR}"
                  export AWS_SECRET_ACCESS_KEY="${AWS_PSW}"
                  export AWS_DEFAULT_REGION="us-west-2"
                  export TF_VAR_LICENSE_KEY=$LICENSE_KEY
                  export TF_VAR_API_KEY=$API_KEY
                  terraform destroy -auto-approve
              '''
                }
              }
    }
  }
}
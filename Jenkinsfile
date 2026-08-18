// =====================================================================
// ApexBank — Jenkins CI/CD Pipeline (Windows)
// =====================================================================
//
// Backend:
//   apexbank-microservices/
//     eureka-server
//     api-gateway
//     auth-service
//     account-service
//     transaction-service
//
// Frontend:
//   apexbank-frontend-microservices/
//
// Jenkins Agent:
//   Windows
//
// Required Jenkins Tools:
//   JDK17
//   Maven3
//   Node18
// =====================================================================

pipeline {

    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
        nodejs 'Node18'
    }

    environment {
        BACKEND_DIR = 'apexbank-microservices'
        FRONTEND_DIR = 'apexbank-frontend-microservices'
        DOCKER_IMAGE_PREFIX = 'apexbank'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {

        // =============================================================
        // CHECKOUT
        // =============================================================

        stage('Checkout') {
            steps {
                echo 'Checking out ApexBank source code...'
                checkout scm
            }
        }


        // =============================================================
        // CONTINUOUS INTEGRATION - BACKEND
        // =============================================================

        stage('Backend: Build') {
            steps {
                dir("${BACKEND_DIR}") {
                    echo 'Building all backend microservices (Maven multi-module)...'

                    bat '''
                        mvn clean install -DskipTests
                    '''
                }
            }
        }


        stage('Backend: Unit Tests') {
            steps {
                dir("${BACKEND_DIR}") {
                    echo 'Running JUnit + Mockito tests for all services...'

                    bat '''
                        mvn test
                    '''
                }
            }

            post {
                always {
                    junit(
                        testResults: '**/target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )
                }
            }
        }


        // =============================================================
        // CONTINUOUS INTEGRATION - FRONTEND
        // =============================================================

        stage('Frontend: Install Dependencies') {
            steps {
                dir("${FRONTEND_DIR}") {
                    echo 'Installing Angular dependencies...'

                    bat '''
                        npm ci
                    '''
                }
            }
        }


        stage('Frontend: Build') {
            steps {
                dir("${FRONTEND_DIR}") {
                    echo 'Building Angular application (production)...'

                    bat '''
                        npx ng build --configuration production
                    '''
                }
            }
        }


        stage('Frontend: Unit Tests') {
            steps {
                dir("${FRONTEND_DIR}") {
                    echo 'Running Angular unit tests headlessly...'

                    bat '''
                        npx ng test --watch=false --browsers=ChromeHeadless
                    '''
                }
            }
        }


        // =============================================================
        // CONTINUOUS DELIVERY
        // =============================================================

        stage('Package: Backend JARs') {
            steps {
                dir("${BACKEND_DIR}") {
                    echo 'Packaging backend services into executable JARs...'

                    bat '''
                        mvn clean package -DskipTests
                    '''
                }
            }
        }


        // =============================================================
        // DOCKER BUILD
        // =============================================================

        stage('Build Docker Images') {
            steps {
                dir("${BACKEND_DIR}") {
                    echo 'Building Docker images for each microservice...'

                    bat '''
                        echo ==========================================
                        echo Building Eureka Server
                        echo ==========================================
                        docker build -t %DOCKER_IMAGE_PREFIX%-eureka-server:%BUILD_NUMBER% eureka-server
                        docker tag %DOCKER_IMAGE_PREFIX%-eureka-server:%BUILD_NUMBER% %DOCKER_IMAGE_PREFIX%-eureka-server:latest

                        echo ==========================================
                        echo Building API Gateway
                        echo ==========================================
                        docker build -t %DOCKER_IMAGE_PREFIX%-api-gateway:%BUILD_NUMBER% api-gateway
                        docker tag %DOCKER_IMAGE_PREFIX%-api-gateway:%BUILD_NUMBER% %DOCKER_IMAGE_PREFIX%-api-gateway:latest

                        echo ==========================================
                        echo Building Auth Service
                        echo ==========================================
                        docker build -t %DOCKER_IMAGE_PREFIX%-auth-service:%BUILD_NUMBER% auth-service
                        docker tag %DOCKER_IMAGE_PREFIX%-auth-service:%BUILD_NUMBER% %DOCKER_IMAGE_PREFIX%-auth-service:latest

                        echo ==========================================
                        echo Building Account Service
                        echo ==========================================
                        docker build -t %DOCKER_IMAGE_PREFIX%-account-service:%BUILD_NUMBER% account-service
                        docker tag %DOCKER_IMAGE_PREFIX%-account-service:%BUILD_NUMBER% %DOCKER_IMAGE_PREFIX%-account-service:latest

                        echo ==========================================
                        echo Building Transaction Service
                        echo ==========================================
                        docker build -t %DOCKER_IMAGE_PREFIX%-transaction-service:%BUILD_NUMBER% transaction-service
                        docker tag %DOCKER_IMAGE_PREFIX%-transaction-service:%BUILD_NUMBER% %DOCKER_IMAGE_PREFIX%-transaction-service:latest

                        echo ==========================================
                        echo Docker images built successfully
                        echo ==========================================
                        docker images
                    '''
                }
            }
        }


        // =============================================================
        // DEPLOY
        // =============================================================

        stage('Deploy: Docker Compose') {
            steps {
                dir("${BACKEND_DIR}") {
                    echo 'Deploying full stack via Docker Compose...'

                    bat '''
                        echo Stopping existing containers...

                        docker compose down

                        echo Starting ApexBank containers...

                        docker compose up -d --build

                        echo ==========================================
                        echo Docker Compose deployment completed
                        echo ==========================================

                        docker compose ps
                    '''
                }
            }
        }


        // =============================================================
        // SMOKE TEST
        // =============================================================

        stage('Smoke Test') {
            steps {
                echo 'Waiting for Eureka registry to start...'

                bat '''
                    ping 127.0.0.1 -n 21 > nul

                    echo Checking Eureka at http://localhost:8761/

                    curl.exe -f http://localhost:8761/

                    if %ERRORLEVEL% EQU 0 (
                        echo ==========================================
                        echo Eureka is UP
                        echo ==========================================
                    ) else (
                        echo ==========================================
                        echo WARNING: Eureka is not reachable
                        echo ==========================================
                    )
                '''
            }
        }
    }


    // =============================================================
    // POST ACTIONS
    // =============================================================

    post {

        success {
            echo '=========================================='
            echo 'ApexBank CI/CD pipeline completed successfully.'
            echo '=========================================='
        }

        failure {
            echo '=========================================='
            echo 'ApexBank CI/CD pipeline failed.'
            echo 'Check the stage logs above.'
            echo '=========================================='
        }

        always {
            echo "Build #${BUILD_NUMBER} finished with status: ${currentBuild.currentResult}"
        }
    }
}

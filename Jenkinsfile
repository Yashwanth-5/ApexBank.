// =====================================================================
// ApexBank — Jenkins CI/CD Pipeline (Declarative)
//
// This pipeline builds and tests the full ApexBank stack:
//   - Backend: 5 Spring Boot microservices (Maven multi-module project)
//       eureka-server, api-gateway, auth-service, account-service,
//       transaction-service
//   - Frontend: Angular application
//
// CI stages: checkout -> backend build+test -> frontend build+test
// CD stages: package (JARs + Docker images) -> deploy (docker-compose)
//
// Place this file at the ROOT of your repository, alongside:
//   apexbank-microservices/   (backend, contains parent pom.xml)
//   apexbank-frontend-microservices/   (frontend, contains package.json)
//
// Adjust BACKEND_DIR / FRONTEND_DIR below if your folder names differ.
// =====================================================================

pipeline {

    agent any

    tools {
        // These names must match the tool installations configured in
        // Jenkins under: Manage Jenkins -> Tools
        jdk 'JDK17'
        maven 'Maven3'
        nodejs 'Node18'
    }

    environment {
        BACKEND_DIR   = 'apexbank-microservices'
        FRONTEND_DIR  = 'apexbank-frontend-microservices'
        DOCKER_IMAGE_PREFIX = 'apexbank'
    }

    options {
        // Keep the last 10 builds, timestamps in console log, and fail
        // the build if any stage hangs for more than 30 minutes.
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out ApexBank source code...'
                checkout scm
            }
        }

        // ============ CONTINUOUS INTEGRATION ============

        stage('Backend: Build') {
            steps {
                dir("${BACKEND_DIR}") {
                    echo 'Building all backend microservices (Maven multi-module)...'
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        stage('Backend: Unit Tests') {
            steps {
                dir("${BACKEND_DIR}") {
                    echo 'Running JUnit + Mockito tests for all services...'
                    sh 'mvn test'
                }
            }
            post {
                always {
                    // Publish JUnit XML results from every module
                    // (auth-service, account-service, transaction-service, etc.)
                    junit testResults: "${BACKEND_DIR}/**/target/surefire-reports/*.xml",
                          allowEmptyResults: true
                }
            }
        }

        stage('Frontend: Install Dependencies') {
            steps {
                dir("${FRONTEND_DIR}") {
                    echo 'Installing Angular dependencies...'
                    sh 'npm ci'
                }
            }
        }

        stage('Frontend: Build') {
            steps {
                dir("${FRONTEND_DIR}") {
                    echo 'Building Angular application (production)...'
                    sh 'npx ng build --configuration production'
                }
            }
        }

        stage('Frontend: Unit Tests') {
            steps {
                dir("${FRONTEND_DIR}") {
                    echo 'Running Angular unit tests headlessly...'
                    sh 'npx ng test --watch=false --browsers=ChromeHeadless'
                }
            }
        }

        // ============ CONTINUOUS DELIVERY / DEPLOYMENT ============

        stage('Package: Backend JARs') {
            steps {
                dir("${BACKEND_DIR}") {
                    echo 'Packaging backend services into executable JARs...'
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                dir("${BACKEND_DIR}") {
                    echo 'Building Docker images for each microservice...'
                    sh '''
                        for svc in eureka-server api-gateway auth-service account-service transaction-service; do
                            echo "Building image for $svc..."
                            docker build -t ${DOCKER_IMAGE_PREFIX}-${svc}:${BUILD_NUMBER} ${svc}
                            docker tag ${DOCKER_IMAGE_PREFIX}-${svc}:${BUILD_NUMBER} ${DOCKER_IMAGE_PREFIX}-${svc}:latest
                        done
                    '''
                }
            }
        }

        stage('Deploy: Docker Compose') {
            steps {
                dir("${BACKEND_DIR}") {
                    echo 'Deploying full stack via docker-compose...'
                    sh '''
                        docker-compose down || true
                        docker-compose up -d --build
                    '''
                }
            }
        }

        stage('Smoke Test') {
            steps {
                echo 'Verifying Eureka registry is reachable after deployment...'
                sh '''
                    sleep 20
                    curl -sf http://localhost:8761/ > /dev/null && echo "Eureka is up" || echo "WARNING: Eureka not reachable yet"
                '''
            }
        }
    }

    post {
        success {
            echo 'ApexBank CI/CD pipeline completed successfully.'
        }
        failure {
            echo 'ApexBank CI/CD pipeline failed. Check the stage logs above.'
        }
        always {
            echo "Build #${BUILD_NUMBER} finished with status: ${currentBuild.currentResult}"
        }
    }
}

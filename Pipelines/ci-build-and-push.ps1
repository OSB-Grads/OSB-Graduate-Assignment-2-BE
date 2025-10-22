
$ErrorActionPreference = "Stop"

# Function to log messages and flush immediately
function Log($message) {
Write-Host $message
Start-Sleep -Milliseconds 100
}

# Arguments from pipeline
$ACR_NAME = $args[0]
$IMAGE_NAME = $args[1]
$DOCKERFILE_PATH = $args[2]
$IMAGE_TAG = $args[3]

Log "-------------------------------------------"
Log "Starting CI Build & Push Docker Image"
Log "ACR: $ACR_NAME"
Log "Image: $IMAGE_NAME"
Log "Dockerfile: $DOCKERFILE_PATH"
Log "Tag: $IMAGE_TAG"
Log "-------------------------------------------"

Log "Logging into ACR..."
az acr login --name $ACR_NAME

Log "Building Docker image..."
docker build -t "${ACR_NAME}.azurecr.io/${IMAGE_NAME}:${IMAGE_TAG}" -f $DOCKERFILE_PATH .

Log "Pushing Docker image to ACR..."
docker push "${ACR_NAME}.azurecr.io/${IMAGE_NAME}:${IMAGE_TAG}"

Log "CI Build & Push Complete!"
Log "-------------------------------------------"

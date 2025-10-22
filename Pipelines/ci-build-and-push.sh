#!/bin/bash
# Exit on any error and print all commands
set -e
set -o pipefail
set -x

# Function to log messages and flush immediately
log() {
  echo "$1"
  sleep 0.1
}

# Arguments from pipeline
ACR_NAME=$1
IMAGE_NAME=$2
DOCKERFILE_PATH=$3
IMAGE_TAG=$4

log "-------------------------------------------"
log "Starting CI Build & Push Docker Image"
log "ACR: $ACR_NAME"
log "Image: $IMAGE_NAME"
log "Dockerfile: $DOCKERFILE_PATH"
log "Tag: $IMAGE_TAG"
log "-------------------------------------------"

log "Logging into ACR..."
az acr login --name $ACR_NAME

log "Building Docker image..."
docker build -t $ACR_NAME.azurecr.io/$IMAGE_NAME:$IMAGE_TAG -f $DOCKERFILE_PATH .

log "Pushing Docker image to ACR..."
docker push $ACR_NAME.azurecr.io/$IMAGE_NAME:$IMAGE_TAG

log "CI Build & Push Complete!"
log "-------------------------------------------"

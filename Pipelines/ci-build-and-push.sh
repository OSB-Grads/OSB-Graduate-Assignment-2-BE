#!/bin/bash
set -e

# Arguments passed from pipeline
ACR_NAME=$1
IMAGE_NAME=$2
DOCKERFILE_PATH=$3
IMAGE_TAG=$4

echo " Logging into ACR..."
az acr login --name $ACR_NAME

echo " Building Docker image..."
docker build -t $ACR_NAME.azurecr.io/$IMAGE_NAME:$IMAGE_TAG -f $DOCKERFILE_PATH .

echo " Pushing Docker image to ACR..."
docker push $ACR_NAME.azurecr.io/$IMAGE_NAME:$IMAGE_TAG

echo " CI Build & Push Complete!"

#!/bin/bash
set -e

RESOURCE_GROUP=$1
AKS_CLUSTER=$2
NAMESPACE=$3
DEPLOYMENT_FILE=$4
SECRET_FILE=$5
ACR_NAME=$6
IMAGE_NAME=$7
IMAGE_TAG=$8

echo " Connecting to AKS..."
az aks get-credentials --resource-group $RESOURCE_GROUP --name $AKS_CLUSTER --overwrite-existing

echo " Updating image tag in deployment file..."
#!/bin/bash
set -e

RESOURCE_GROUP=$1
AKS_CLUSTER=$2
NAMESPACE=$3
DEPLOYMENT_FILE=$4
SECRET_FILE=$5
ACR_NAME=$6
IMAGE_NAME=$7
IMAGE_TAG=$8

echo " Connecting to AKS..."
az aks get-credentials --resource-group $RESOURCE_GROUP --name $AKS_CLUSTER --overwrite-existing

echo " Updating image tag in deployment file..."
sed -i "s|__ACR_NAME__|$ACR_NAME|g" $DEPLOYMENT_FILE
sed -i "s|__IMAGE_NAME__|$IMAGE_NAME|g" $DEPLOYMENT_FILE
sed -i "s|__IMAGE_TAG__|$IMAGE_TAG|g" $DEPLOYMENT_FILE

echo " Applying secrets..."
kubectl apply -f $SECRET_FILE -n $NAMESPACE

echo " Deploying updated backend..."
kubectl apply -f $DEPLOYMENT_FILE -n $NAMESPACE

echo " Deployment completed successfully!"


echo " Applying secrets..."
kubectl apply -f $SECRET_FILE -n $NAMESPACE

echo " Deploying updated backend..."
kubectl apply -f $DEPLOYMENT_FILE -n $NAMESPACE

echo " Deployment completed successfully!"

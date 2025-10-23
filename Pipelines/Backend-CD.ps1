# Exit on any error
$ErrorActionPreference = "Stop"

# Function to log messages and flush immediately
function Log($message) {
Write-Host $message
Start-Sleep -Milliseconds 100
}

# Arguments from pipeline
$RESOURCE_GROUP = $args[0]
$AKS_CLUSTER = $args[1]
$NAMESPACE = $args[2]
$DEPLOYMENT_FILE= $args[3]
$SECRET_FILE = $args[4]
$ACR_NAME = $args[5]
$IMAGE_NAME = $args[6]
$IMAGE_TAG = $args[7]

Log "-------------------------------------------"
Log "Starting AKS Deployment"
Log "Resource Group: $RESOURCE_GROUP"
Log "AKS Cluster: $AKS_CLUSTER"
Log "Namespace: $NAMESPACE"
Log "Deployment File: $DEPLOYMENT_FILE"
Log "Secret File: $SECRET_FILE"
Log "ACR: $ACR_NAME"
Log "Image: $IMAGE_NAME"
Log "Tag: $IMAGE_TAG"
Log "-------------------------------------------"

Log "Connecting to AKS..."
az aks get-credentials --resource-group $RESOURCE_GROUP --name $AKS_CLUSTER --overwrite-existing

Log "Updating image tag in deployment file..."
(Get-Content $DEPLOYMENT_FILE) `
-replace "__ACR_NAME__", $ACR_NAME `
-replace "__IMAGE_NAME__", $IMAGE_NAME `
-replace "__IMAGE_TAG__", $IMAGE_TAG | Set-Content $DEPLOYMENT_FILE

Log "Applying secrets..."
kubectl apply -f $SECRET_FILE -n $NAMESPACE

Log "Deploying updated backend..."
kubectl apply -f $DEPLOYMENT_FILE -n $NAMESPACE

Log "Deployment completed successfully!"
Log "-------------------------------------------"

# Exit on any error
$ErrorActionPreference = "Stop"

# Function to log messages with timestamp
function Log($message) {
    $ts = (Get-Date).ToString("yyyy-MM-dd HH:mm:ss")
    Write-Host "[$ts] $message"
}

# ------------------------------
# Arguments passed from pipeline
# ------------------------------
$RESOURCE_GROUP = $args[0]
$AKS_CLUSTER    = $args[1]
$NAMESPACE      = $args[2]
$DEPLOYMENT_FILE= $args[3]
$SERVICE_FILE   = $args[4]
$SECRET_FILE    = $args[5]
$ACR_NAME       = $args[6]
$IMAGE_NAME     = $args[7]
$IMAGE_TAG      = $args[8]

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

# ------------------------------
# Validate files
# ------------------------------
if (-not (Test-Path $DEPLOYMENT_FILE)) {
    Log "ERROR: Deployment file not found: $DEPLOYMENT_FILE"
    exit 1
}
if (-not (Test-Path $SECRET_FILE)) {
    Log "ERROR: Secret file not found: $SECRET_FILE"
    exit 1
}

# ------------------------------
# Connect to AKS
# ------------------------------
Log "Connecting to AKS..."
az aks get-credentials --resource-group $RESOURCE_GROUP --name $AKS_CLUSTER --overwrite-existing

# ------------------------------
# Apply secret YAML
# ------------------------------
Log "Applying secrets from $SECRET_FILE..."
kubectl apply -f $SECRET_FILE -n $NAMESPACE

# ------------------------------
# Deploy backend
# ------------------------------
Log "Deploying backend using $DEPLOYMENT_FILE..."
kubectl apply -f $DEPLOYMENT_FILE -n $NAMESPACE
kubectl apply -f $SERVICE_FILE -n $NAMESPACE

Log "Deployment completed successfully!"
Log "-----------------------------
--------------"

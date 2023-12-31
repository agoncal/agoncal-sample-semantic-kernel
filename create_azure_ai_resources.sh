#!/usr/bin/env bash

echo "Seting up environment variables..."
echo "----------------------------------"
PROJECT="semantic-kernel"
RESOURCE_GROUP="rg-$PROJECT"
COGNITIVE_SERVICE="cognit-$PROJECT"
COGNITIVE_DEPLOYMENT="deploy-$PROJECT"
LOCATION="eastus"
TAG="$PROJECT"

echo PROJECT = $PROJECT
echo RESOURCE_GROUP = $RESOURCE_GROUP
echo COGNITIVE_SERVICE = $COGNITIVE_SERVICE
echo COGNITIVE_DEPLOYMENT = $COGNITIVE_DEPLOYMENT
echo LOCATION = $LOCATION
echo TAG = $TAG


echo "Creating the resource group..."
echo "------------------------------"
az group create \
  --name "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --tags system="$TAG"


# To know which SKUs are available, run:
#az cognitiveservices account list-skus \
#  --location "$LOCATION"
# To know which kinds are available, run:
#az cognitiveservices account list-kinds

echo "Creating the Cognitive Service..."
echo "---------------------------------"
az cognitiveservices account create \
  --name "$COGNITIVE_SERVICE" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --custom-domain "$COGNITIVE_SERVICE" \
  --tags system="$TAG" \
  --kind "OpenAI" \
  --sku "S0"

# To know which models are available, run:
az cognitiveservices account list-models \
  --name "$COGNITIVE_SERVICE" \
  --resource-group "$RESOURCE_GROUP"


echo "Deploying the model..."
echo "----------------------"
az cognitiveservices account deployment create \
  --name "$COGNITIVE_SERVICE" \
  --resource-group "$RESOURCE_GROUP" \
  --deployment-name "$COGNITIVE_DEPLOYMENT" \
  --model-name "gpt-35-turbo" \
  --model-version "0301"  \
  --model-format "OpenAI" \
  --scale-settings-scale-type "Standard"


#az cognitiveservices account deployment show \
#  --name "$COGNITIVE_SERVICE" \
#  --resource-group "$RESOURCE_GROUP" \
#  --deployment-name "$COGNITIVE_DEPLOYMENT"


echo "Storing the key and endpoint in environment variables..."
echo "--------------------------------------------------------"
AZUREOPENAI_KEY=$(
  az cognitiveservices account keys list \
    --name "$COGNITIVE_SERVICE" \
    --resource-group "$RESOURCE_GROUP" \
    | jq -r .key1
)
AZUREOPENAI_ENDPOINT=$(
  az cognitiveservices account show \
    --name "$COGNITIVE_SERVICE" \
    --resource-group "$RESOURCE_GROUP" \
    | jq -r .properties.endpoint
)


# Set the properties
echo "--------------------------------------------------"
echo "The following properties can be copied either to the ./conf.properties or to the ~/.sk/conf.properties file:"
echo "--------------------------------------------------"
echo "client.azureopenai.key=$AZUREOPENAI_KEY"
echo "client.azureopenai.endpoint=$AZUREOPENAI_ENDPOINT"
echo "client.azureopenai.deploymentname=$COGNITIVE_DEPLOYMENT"

# Clean up
#az group delete \
#  --name "$RESOURCE_GROUP" \
#  --yes

#az cognitiveservices account purge \
#  --name "$COGNITIVE_SERVICE" \
#  --resource-group "$RESOURCE_GROUP" \
#  --location "$LOCATION"

#az cognitiveservices account delete \
#  --name "$COGNITIVE_SERVICE" \
#  --resource-group "$RESOURCE_GROUP"

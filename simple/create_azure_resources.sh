#!/usr/bin/env bash

echo "Seting up environment variables..."
echo "----------------------------------"
PROJECT="semantic-kernel"
RESOURCE_GROUP="rg-$PROJECT"
COGNITIVE_SERVICE="cog-$PROJECT"
COGNITIVE_DEPLOYMENT="deploy-$PROJECT"
LOCATION="eastus"
TAG="$PROJECT"


echo "Creating the resource group..."
echo "------------------------------"
az group create \
  --name "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --tags system="$TAG"


echo "Creating the Cognitive Service..."
echo "---------------------------------"
az cognitiveservices account create \
  --name "$COGNITIVE_SERVICE" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --tags system="$TAG" \
  --kind OpenAI \
  --sku S0

# To know which models are available, run:
#az cognitiveservices account list-models \
#  --name "$COGNITIVE_SERVICE" \
#  --resource-group "$RESOURCE_GROUP" \


echo "Deploying the model..."
echo "----------------------"
az cognitiveservices account deployment create \
  --name "$COGNITIVE_SERVICE" \
  --resource-group "$RESOURCE_GROUP" \
  --deployment-name "$COGNITIVE_DEPLOYMENT" \
  --model-name gpt-35-turbo \
  --model-version "0301"  \
  --model-format OpenAI \
  --scale-settings-scale-type "Standard"


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
echo "Copy the following properties to the simple/src/main/resources/conf.properties file:"
echo "--------------------------------------------------"
echo "client.azureopenai.key=$AZUREOPENAI_KEY"
echo "client.azureopenai.endpoint=$AZUREOPENAI_ENDPOINT"
echo "client.azureopenai.deploymentname=$COGNITIVE_DEPLOYMENT"


# Clean up
#az group delete \
#  --name "$RESOURCE_GROUP" \
#  --yes

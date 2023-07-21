#!/usr/bin/env bash

# Set up environment variables
PROJECT="semantic-kernel"
RESOURCE_GROUP="rg-$PROJECT"
COGNITIVE_SERVICE="cog-$PROJECT"
COGNITIVE_DEPLOYMENT="deploy-$PROJECT"
LOCATION="eastus"
TAG="$PROJECT"

# Create the resource group
az group create \
  --name "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --tags system="$TAG"

# Create the Cognitive Service
az cognitiveservices account create \
  --name "$COGNITIVE_SERVICE" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --tags system="$TAG" \
  --kind OpenAI \
  --sku S0

# To know which models are available, run:
az cognitiveservices account list-models \
  --name "$COGNITIVE_SERVICE" \
  --resource-group "$RESOURCE_GROUP" \

# Create the model
az cognitiveservices account deployment create \
  --name "$COGNITIVE_SERVICE" \
  --resource-group "$RESOURCE_GROUP" \
  --deployment-name "$COGNITIVE_DEPLOYMENT" \
  --model-name gpt-35-turbo \
  --model-version "0301"  \
  --model-format OpenAI \
  --scale-settings-scale-type "Standard"

# Store the key and endpoint in environment variables
AZUREOPENAI_KEY=$(
  az cognitiveservices account keys list \
    --name "$COGNITIVE_SERVICE" \
    --resource-group "$RESOURCE_GROUP" \
    | jq -r .key1
)
echo "AZUREOPENAI_KEY=$AZUREOPENAI_KEY"

AZUREOPENAI_ENDPOINT=$(
  az cognitiveservices account show \
    --name "$COGNITIVE_SERVICE" \
    --resource-group "$RESOURCE_GROUP" \
    | jq -r .properties.endpoint
)
echo "AZUREOPENAI_ENDPOINT=$AZUREOPENAI_ENDPOINT"

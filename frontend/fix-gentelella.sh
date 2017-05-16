#!/usr/bin/env bash

mkdir node_modules/gentelella/build/images
cp node_modules/gentelella/production/images/{back,forward,loading}* node_modules/gentelella/build/images

echo "Fixed Gentelella!"

#!/bin/zsh

echo 'cd front'
cd front
rm -rf dist

echo 'build...'
npm run build

echo 'cd clusterhouse'
cd ../clusterhouse
rm -rf src/main/resources/static
mkdir src/main/resources/static

echo 'cp files'
cp ../front/index_prod.html src/main/resources/static/index.html
cp -r ../front/dist src/main/resources/static/
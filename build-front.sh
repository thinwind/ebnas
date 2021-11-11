#!/bin/zsh

echo 'cd front'
cd front
rm -rf dist

echo 'build...'
npm run build

echo 'cd clusterhouse'
cd ../clusterhouse
rm -rf resources/static
mkdir resources/static

echo 'cp files'
cp ../front/index_prod.html resources/static/index.html
cp -r ../front/dist resources/static/
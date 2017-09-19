#!/bin/bash
platform="unknown"
unamestr=`uname`
if [[ "$unamestr" == 'Linux' ]]; then
   platform='Linux'
elif [[ "$unamestr" == 'Darwin' ]]; then
   platform='Mac'
else
   platform='Unknown'
fi

if hash jq 2>/dev/null; then
   echo "jq is already installed"
else
   echo "Beginning to install jq"
   if [[ "$platform" == 'Linux' ]]; then
      apt-get -qq update && apt-get install jq
   elif [[ "$platform" == 'Mac' ]]; then
      brew install jq
   else
      echo "Unknown OS"
      echo "Please install jq manually"
      exit 1
   fi

fi

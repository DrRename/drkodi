#!/usr/bin/env bash
echo "Main jar:" "$1".jar
/usr/bin/jpackage --name DrRename --input target --main-jar "$1".jar --resource-dir src/main/resources/icons --dest target --app-version 1.0.0 --copyright copyrightstring --description descriptionstring --vendor vendorstring --java-options '--enable-preview'
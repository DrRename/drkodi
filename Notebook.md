#  Notebook

+ `productbuild --component /Users/alex/sources/drrename/target/DrRename/DrRename.app/ /Applications --sign "3rd Party Mac Developer Installer: Alexander Kerner (5W26Y2F3CM)" --product /Users/alex/sources/drrename/target/DrRename/DrRename.app/Contents/Info.plist /Users/alex/sources/drrename/target/DrRename_1.0.26.pkg`
+ `xcrun altool --upload-app --type osx --apiKey "NMQU7U7Z5G" --apiIssuer "3a8a5081-5288-41dd-8527-b6559074128a" -f /Users/alex/sources/drrename/target/DrRename_1.0.26.pkg`
+ `shc -r -f universalJavaApplicationStub -v -D` 
+ `clang -o target/DrRename/DrRename.app/Contents/MacOS/launcher.x86_64 -target x86_64-apple-macos10.10 target/DrRename/DrRename.app/Contents/MacOS/launcher.x.c`
+ shc -r -f launcher
+ clang -o target/launcher.x86_64 -target x86_64-apple-macos10.10 launcher.x.c 
+ cp target/launcher.x86_64 ~/sources/drrename/assets/mac/launcher
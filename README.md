# Dr.Rename

[![Build](https://github.com/drrename/drrename/actions/workflows/build.yml/badge.svg)](https://github.com/drrename/drrename/actions/workflows/build.yml)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drrename&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=DrRename_drrename)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drrename&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=DrRename_drrename)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drrename&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=DrRename_drrename)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drrename&metric=bugs)](https://sonarcloud.io/summary/new_code?id=DrRename_drrename)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drrename&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=DrRename_drrename)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drrename&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=DrRename_drrename)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drrename&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=DrRename_drrename)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drrename&metric=alert_status)](https://sonarcloud.io/dashboard?id=DrRename_drrename)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drrename&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=DrRename_drrename)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drrename&metric=coverage)](https://sonarcloud.io/summary/new_code?id=DrRename_drrename)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drrename&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=DrRename_drrename)
[![Latest Release](https://img.shields.io/github/release/drrename/drrename.svg)](https://github.com/drrename/drrename/releases/latest)

Minimalistic Batch-Renamer

## Prerequirements

You need to have Java **17** installed.

## Installation and Starting

Download [latest Release](https://github.com/drrename/drrename/releases/latest) and call `java -jar drrename-<current-version>.jar`. A double click on the jar-file could also work.

Note that Java needs to be installed.

## Examples

1. Replace any space by an underscore ![example-replace-space-by-underscore.png](./screens/example-replace-space-by-underscore.png)

### Pictures

1. Get extension from MIME type. Note that this will also add missing extensions:
 ![example-add-missing-extension.png](./screens/example-missing-extension.png)

## Kodi Tools

Open Kodi Tools via `File` -> `Kodi Tools`.

Kodi Tools help you to inspect and partly correct a [Kodi](https://kodi.tv/) media library.
The library set up is expected to be as follows (taken from [Kodi Wiki](https://kodi.wiki/view/Naming_video_files/Movies)):

> Each movie is saved in its own folder within the Source.
All files and folders should be simply named with the name of the movie and the year in brackets. The name should match the name shown at the scraper site.
> Each movie file is placed into its own folder which is then added to your Source.
> + Placing movies in their own folder allows saving of local artwork and NFO files alongside the movie file.
> + You have the choice of using the Short or Long name format for the artwork. See: Local Artwork
> + Using this method will provide the safest and most accurate scrape of your media collection.
> + Most library related add-ons will only work correctly with this method.
> + Some skins use modified file naming to display additional Media Flags. These apply to the filename, not the folder name.

Kodi Tools perform the following checks:

1. Look up the movie name (i.e., the *folder* name) on [theMovieDB](https://www.themoviedb.org/). If the exact folder name could not be found on theMovieDB, but suggestions are available, those are offered as a quick fix. Localized titles are suggested depending on your [locale setting](link to locale).
    
   ![kodi-checks-themoviedb-01](./screens/kodi-checks-themoviedb-01.png)

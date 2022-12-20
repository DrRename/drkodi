# Dr.Kodi

[![Build](https://github.com/drrename/drkodi/actions/workflows/build.yml/badge.svg)](https://github.com/drrename/drkodi/actions/workflows/build.yml)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=bugs)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=alert_status)](https://sonarcloud.io/dashboard?id=DrRename_drkodi)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=coverage)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Latest Release](https://img.shields.io/github/release/drkodi/drkodi.svg)](https://github.com/drrename/drkodi/releases/latest)


https://user-images.githubusercontent.com/13817521/208780222-6a594fc5-b926-4bfd-b967-cefcdcc84de7.mov


## Minimalistic Media Library Tool

Dr.Kodi helps you to inspect and partly correct a [Kodi](https://kodi.tv/) media library.

Kodi media player expects the library set up to be as follows (taken from [Kodi Wiki](https://kodi.wiki/view/Naming_video_files/Movies)):

> + Each movie is saved in its own folder within the Source.
All files and folders should be simply named with the name of the movie and the year in brackets. The name should match the name shown at the scraper site.
> + Each movie file is placed into its own folder which is then added to your Source.
> + Placing movies in their own folder allows saving of local artwork and NFO files alongside the movie file.
> + Using this method will provide the safest and most accurate scrape of your media collection.
> + Most library related add-ons will only work correctly with this method.

Since Dr.Kodi does look up all information by itself or reads it from an existing NFO file, folder naming can be less strict. Dr.Kodi will warn you if the folder name does not match the [normalized](link to string normalizatino) movie title or the [normalized](link to string normalizatino) movie original title.

The workflow is as follows:

### Workflow

Initially, Dr.Kodi iterates over all subdirectories of a given path. Every directory found is considered to be a movie. Next, for each movie, the following steps/ checks are performed:

1. Look for an NFO file. If found, update the view using the data found in the NFO file.
2. If no NFO file is found, the [normalized](link to string normalizatino) folder name is used to query [theMovieDB](https://www.themoviedb.org/). Found results are displayed and the user might choose any of those suggestions to take over the according information. Localized titles are suggested depending on your [locale setting](link to locale).

### Installation and running

For now, Dr.Kodi does not bundle a JRE (see https://github.com/DrRename/drkodi/issues/8). To run Dr.Kodi, make sure you have Java 17 or later installed.

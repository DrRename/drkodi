# Dr.Kodi

[![Build](https://github.com/drrename/drkodi/actions/workflows/build.yml/badge.svg)](https://github.com/drrename/drkodi/actions/workflows/build.yml)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=DrRename_drkodi&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=DrRename_drkodi)


https://user-images.githubusercontent.com/13817521/208781276-60301994-3e0d-4172-9ca8-1def39b4f29b.mov


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

#### Windows

Download the latest `jar`, i.e., `drkodi-<version>-win.jar`. Run it by double-clicking the `jar` file or executing `java -jar drkodi-<version>-win.jar`.

**Note:** Java 17 or higher needs to be installed. Verify with `java -version`

#### Linux

Download the latest `jar`, i.e., `drkodi-<version>-linux.jar`. Run it by double-clicking the `jar` file or executing `java -jar drkodi-<version>-linux.jar`.

**Note:** Java 17 or higher needs to be installed. Verify with `java -version`

#### Mac

Download the latest `DrKodi.app.zip`, unpack it and double-click.

**Note:** No Java installation required.

Alternatively, download the latest `jar`, i.e., `drkodi-<version>-mac.jar`. Run it by double-clicking the `jar` file or executing `java -jar drkodi-<version>-mac.jar`.

**Note:** Java 17 or higher needs to be installed. Verify with `java -version`.




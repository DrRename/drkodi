# Dr.Kodi

[![Build](https://github.com/drrename/drkodi/actions/workflows/build.yml/badge.svg)](https://github.com/drrename/drrename/actions/workflows/build.yml)
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

## Minimalistic Media Library Tool

Dr.Kodi helps you to inspect and partly correct a [Kodi](https://kodi.tv/) media library.
Kodi media player expects the library set up to be as follows (taken from [Kodi Wiki](https://kodi.wiki/view/Naming_video_files/Movies)):

> Each movie is saved in its own folder within the Source.
All files and folders should be simply named with the name of the movie and the year in brackets. The name should match the name shown at the scraper site.
> Each movie file is placed into its own folder which is then added to your Source.
> + Placing movies in their own folder allows saving of local artwork and NFO files alongside the movie file.
> + You have the choice of using the Short or Long name format for the artwork. See: Local Artwork
> + Using this method will provide the safest and most accurate scrape of your media collection.
> + Most library related add-ons will only work correctly with this method.
> + Some skins use modified file naming to display additional Media Flags. These apply to the filename, not the folder name.

Dr.Kodi helps you to quickly detect and correct issues with the media library layout. For this, it perform the following checks:

1. Look up the movie name (i.e., the *folder* name) on [theMovieDB](https://www.themoviedb.org/). If no movie matching the exact folder name could be found on theMovieDB, suggestions are available, those are offered as a quick fix. Localized titles are suggested depending on your [locale setting](link to locale).

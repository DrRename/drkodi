
# Dr.Kodi

<img align="right" width="100" height="100" src="drkodi.png">


[![Build](https://github.com/kerner1000/drkodi/actions/workflows/build.yml/badge.svg)](https://github.com/kerner1000/drkodi/actions/workflows/build.yml) [![Latest Release](https://img.shields.io/github/release/kerner1000/drkodi.svg)](https://github.com/kerner1000/drkodi/releases/latest) [![Release Date](https://img.shields.io/github/release-date/kerner1000/drkodi?color=blue)](https://github.com/kerner1000/drkodi/releases/latest) [![License](https://img.shields.io/github/license/kerner1000/drkodi.svg)](https://github.com/kerner1000/drkodi/blob/master/LICENSE)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Tools1000_drkodi&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Tools1000_drkodi)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Tools1000_drkodi&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Tools1000_drkodi)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Tools1000_drkodi&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Tools1000_drkodi)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Tools1000_drkodi&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Tools1000_drkodi)

[//]: # (https://user-images.githubusercontent.com/13817521/208781276-60301994-3e0d-4172-9ca8-1def39b4f29b.mov)


## Minimalistic Media Library Tool

Dr.Kodi helps you to inspect and partly correct a [Kodi](https://kodi.tv/) media library.

Kodi media player expects the library set up to be as follows (taken from [Kodi Wiki](https://kodi.wiki/view/Naming_video_files/Movies)):

> + Each movie is saved in its own folder within the Source.
All files and folders should be simply named with the name of the movie and the year in brackets. The name should match the name shown at the scraper site.
> + Each movie file is placed into its own folder which is then added to your Source.
> + Placing movies in their own folder allows saving of local artwork and NFO files alongside the movie file.
> + Using this method will provide the safest and most accurate scrape of your media collection.
> + Most library related add-ons will only work correctly with this method.

Since Dr.Kodi does look up all information by itself or reads it from an existing NFO file, folder naming can be less strict.

## Checks

The following checks are performed. For most, an automatic fix is offered.

1. **The path should be a directory and not a file** (see _Flat Folder_ (https://kodi.wiki/view/Naming_video_files/Movies). Dr.Kodi offers a quick fix to wrap the media file into a directory of the same name.
2. **The directory should not be empty and should contain at least one media file**.
3. **The media file** (or multiple files, see _Split Video Files_ (https://kodi.wiki/view/Naming_video_files/Movies)) **needs to have the same name as the folder**.
4. **The directory should not have subdirectories** (e.g., a _Subs_ folder containing subtitles is not supported).

Those basic checks can be performed offline and without the presence of an NFO file (see https://kodi.wiki/view/NFO_files). For the following checks, information from an NFO file is considered as well.

1. **The folder name should match the** [normalized](#title-normalization) **NFO's movie title**.
2. **The NFO's movie year** (release year) **should match the folder's movie year** (folder naming pattern: _The Matrix (1998)_, see _Naming_ (https://kodi.wiki/view/Naming_video_files/Movies)) 

### Example Walk-through for a Movies Library

We start with a very small library that contains one movie. The 'library' is just the parent folder, in this case _Movies_.

<img width="666" alt="Screenshot 2023-01-08 at 10 03 26" src="https://user-images.githubusercontent.com/13817521/211188313-3593698a-7e77-45d5-854b-64c68653aa1e.png">

We Drag & Drop it into the Main View. After selecting 'die hard.mkv', details are shown on the right.

<img width="729" alt="Screenshot 2023-01-08 at 10 07 51" src="https://user-images.githubusercontent.com/13817521/211188423-757be751-6ca6-4595-b7ae-00bf74e1b2c2.png">

_Not a directory_ warning is shown and a quick-fix is offered. Applying the fix will wrap the media file in a directory of the same name.

<img width="807" alt="Screenshot 2023-01-08 at 11 01 26" src="https://user-images.githubusercontent.com/13817521/211190123-6f341061-044f-44a6-b075-077d358e7806.png">

After applying the fix the [normalized](#title-normalization) movie title is used to query [themoviedb](#themoviedb-lookup) to complete movie meta data. Search results are shown below the information that is already present. If Dr.Kodi detects a non-default locale, every search result is presented in the default locale (en) and as well in the current locale. In this case, an additional, localized search result is presented.

en locale | de locale
--------- | ---------
<img width="1025" alt="Screenshot 2023-01-08 at 11 25 07" src="https://user-images.githubusercontent.com/13817521/211191042-29f515e7-409f-4ef2-b8c6-90e22f9c8149.png"> | <img width="1025" alt="Screenshot 2023-01-08 at 11 24 39" src="https://user-images.githubusercontent.com/13817521/211191026-6c6f2337-c9bd-47c9-9115-f7578a6251b4.png">
 
After taking over a search result, the movie meta data is stored as an NFO file inside the directory.

<img width="666" alt="Screenshot 2023-01-08 at 11 26 18" src="https://user-images.githubusercontent.com/13817521/211191099-3276a9f6-8171-4895-beb9-2ac22e098e42.png">

_Folder year does not match_ warning is shown and a quick-fix is offered. Applying the fix will rename the directory after the movie title plus year (folder naming pattern: _Die Hard (1988)_, see _Naming_ (https://kodi.wiki/view/Naming_video_files/Movies)). 

<img width="943" alt="Screenshot 2023-01-08 at 13 20 08" src="https://user-images.githubusercontent.com/13817521/211195708-79761187-7fc6-4e47-924e-ad2e63013236.png">

<img width="943" alt="Screenshot 2023-01-08 at 13 20 53" src="https://user-images.githubusercontent.com/13817521/211195721-d3345861-3def-478b-b980-2a3380521dbd.png">

<img width="666" alt="Screenshot 2023-01-08 at 13 23 58" src="https://user-images.githubusercontent.com/13817521/211195869-04ddd423-05ba-4aee-84c3-d430eafd0b03.png">


### Title Normalization

#### Normalization Examples

+ <img width="957" alt="Screenshot 2023-01-08 at 13 11 34" src="https://user-images.githubusercontent.com/13817521/211195353-0a87f5a7-61a7-4086-be9f-d2072803e087.png">

'Input' string, i.e., the 'unnormalized' query string is `Der.Schatzplanet.2002.German.DL.1080p.BluRay.x264-ENCOUNTERS`. The normalized string, that is used to query themoviedb is `Der Schatzplanet`.

+ <img width="1138" alt="Screenshot 2023-01-08 at 13 14 18" src="https://user-images.githubusercontent.com/13817521/211195454-c5e8a55c-3ecf-4763-bdad-a489f22f3cd2.png">

'Input' string, i.e., the 'unnormalized' query string is `Coming.Home.for.Christmas.Eine.Familie.zur.Bescherung.German.2013.AC3.DVDRiP.x264-SAViOUR`. The normalized string, that is used to query themoviedb is `Coming Home for Christmas Eine Familie zur Bescherung`.


### themoviedb Lookup

If data is incomplete, the [normalized](#title-normalization) movie title is used to query [theMovieDB](https://www.themoviedb.org/). Found results are displayed and the user might choose any of those suggestions to take over the according information. Localized titles are suggested depending on your [locale setting](link to locale).

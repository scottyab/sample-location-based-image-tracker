# Scott's Sample App Interview Challenge

The goal of this sample app is to demonstrate my technical ability and preferred working architecture completed within couple of days. 

### Deliverables  
* [Loom video](https://loom.com/share/0b10dc86fc114798ab3127ab93eba920) **Watch this first** as 
  it walks through this readme, demos the app and mentions key classes for review. This is 
  typical of 
  the type of video I 
  would 
  record 
  for a PR review or feature demo. 
* [app-release.apk](deliverables/app-release-v1.1.apk) - This is the release apk.
* This readme which details the up front analysis, planning and thought processes. 

### Important classes for review

* [RealSnapshotRepository](https://github.com/scottyab/sample-location-based-image-tracker/blob/main/app/src/main/java/com/scottyab/challenge/data/RealSnapshotRepository.kt) and it's [test](https://github.com/scottyab/sample-location-based-image-tracker/blob/main/app/src/test/java/com/scottyab/challenge/data/RealSnapshotRepositoryTest.kt)
* [SnapshotTracker](https://github.com/scottyab/sample-location-based-image-tracker/blob/main/app/src/main/java/com/scottyab/challenge/domain/SnapshotTracker.kt) and it's [test](https://github.com/scottyab/sample-location-based-image-tracker/blob/main/app/src/test/java/com/scottyab/challenge/domain/SnapshotTrackerTest.kt)
* [SnapshotViewModel](https://github.com/scottyab/sample-location-based-image-tracker/blob/main
/app/src/main/java/com/scottyab/challenge/presentation/snapshots/SnapshotsViewModel.kt) and it's [test](https://github.com/scottyab/sample-location-based-image-tracker/blob/main/app/src/test/java/com/scottyab/challenge/presentation/snapshots/SnapshotsViewModelTest.kt) 


## Brief: Location based image tracker

Develop an Android app that enables you to track your walk with images.

The user opens the app and presses the start button. After that the user puts their phone into their pocket and starts walking. The app requests a photo from the public flickr photo search api for his location every 100 meters to add to the stream. New pictures are added on top. Whenever the user takes a look at their phone, they see the most recent picture and can scroll through a stream of pictures which shows where the user has been. It should work for at least a two-hour walk. The user interface should be simple as shown on the left of this page.

* Photos must be more than 100m from each other 
  

## Discover and Planning
First part of this (or any) task is to establish understanding of the requirements, domain, 
define assumptions and unknowns.

### Assumptions
* The minSdkVersion will be 26 to achieve a device distribution of 90%+ (as noted in the Android Studio Project setup) this also allowed to use of more modern Java Date Time APIs (this saved time given the constricted timeline)
* We can rely on Google Play services being present on the device.
* Handling the user (or system) revoking the location permission after the initial grant is out of scope (something we will solve later.)
* The users of this app are smartphone-sized devices and not larger Android tablets or Watches etc
* Given the tracker should work for at least a two-hour activity for rough capacity planning. I’ve estimated a 5 kilometres per hour walking speed, this equates to a distance travelled of 10km. If there’s a unique Flickr photo for each of the 100m location updates the app needs to support approximately 100 images/snapshots in total.

### Disclaimers due to constricted timeline
* I’ve selected an architecture (i.e MVVM, Clean) and 3rd party libraries (i.e as Moshi, Retrofit, Coil etc) as I feel they are appropriate in this scenario and I’ve used them before and know they are common so that onboarding new team members would not be an issue. However, I haven’t extensively researched and validated these choices for this use case.
* If this was a green field real life project I’d use Jetpack Compose for the UI however I’m more familiar with Android View I didn’t want to expose unnecessary risk to the project.
* Flickr location based content varies depending on location if there are limited photos in the location the device is in. Duplicate photos (or as I’m referring to them Snapshots) are discarded as it is assumed these are not desired by the user.

### Unknowns at the start of this task 
* Flickr API - create account, read API docs to understand params needed for the [photo search 
  api](https://www.flickr.com/services/api/flickr.photos.search.html) 
* Emulating location changes - read Android SDK docs on how to mock locations which will be 
  essential for testing given that location changes are the main trigger for app events. 

## Architecture and Tech stack
* MVVM for the presentation architecture 
* Clean style architecture for the app i.e presentation, domain and data. 

Here's the rough Architecture sketch I completed ahead of starting the implementation. Typically 
this would be something I'd add to tech spec or share with team to seek peer review before 
starting task.  

[Arch diagram](/deliverables/challenge-arch.png) 

## Task list

* Base project with ktlint static code analysis, dependencies, DI
* Setup Remote Datasource / Flickr Retrofit and API mapping code
* Room Database and Snapshots Entity
* Snapshot Repository abstract data + tests
* Snapshot Tracker to manager the whether we are actively tracking + tests
* Get basic Snapshots UI, Activity, Adapter
* Add populate data feature
* Create some test data
* Test hardness to load photos from Flickr and save them as snapshots into the db and load this 
in the UI
* Photo duplicate detection
* Add Clear/Delete all functionality
* Add Real Location Foreground service/Provider
* Manual testing with Emulator and real device

### Tasks if I had more time
* Error handling adding Snapshot i.e on database or internet connection timeout or error. 
Including API error handling i.e invalid api_key or error 500
* Related to the above support for when internet connection being down when location update occurs.
* Add Coroutine error handling
* Handling when Location permission is revoked or only coarse location permission is granted.
* Also as per Google UX recommendations add rational UI to justify why the Location permission is 
needed.
* Reevaluate the decision to store lat/long as strings - Doubles would likely be more appropriate.
* Update the UI to match the designs. I had some difficulty wrestling with the Android theming 
system attempting to get the drop shadow on the app bar. I’m sure with fresh coffee I could resolve this.
* Also error images, placeholders, loading image
* More unit tests API parsing and a couple of UI and/or instrumentation tests to give help test 
RealLocationProvider and LocationService.
* Remove the Timber log statements
* Modularise the app into presentation/app, domain and data to better enforce the layer 
separation and take advantage of Gradle optimisations
* Either limit the number of snapshots that can be loaded or look at implementing pagination of 
the data from the Room DB i.e Android X Pagination library.

### Extra features
Some notes how I might look to enhance this further outside of the requirements

* Show route / map
* Summary of activity i.e distance, calories, num of snapshots
* Support multiple activities
* Allow photos to be taken and add them as Snapshots
* Share multiple images  
* Analytics

 

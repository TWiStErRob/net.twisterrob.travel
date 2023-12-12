### -- android/app/full/proguard.pro -- ###

# Debugging helpers
#-dontobfuscate
#-dontoptimize
#-optimizationpasses 2

# See res/menu/main.xml / stations_list.xml and b.android.com/170471
-keep class android.support.v7.widget.SearchView { <init>(...); }

# Not used in Android, but in Shared
-dontwarn net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedKmlOut

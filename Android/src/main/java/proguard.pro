### -- BLT/proguard.pro -- ###

# Debugging helpers
#-dontobfuscate
#-dontoptimize
#-optimizationpasses 2

# See res/menu/main.xml / stations_list.xml and b.android.com/170471
-keep class android.support.v7.widget.SearchView { <init>(...); }

# Google Play Service (GMS) internal stuff
# I have nothing to do with these
-dontnote com.google.android.gms.**
-dontnote com.google.android.gms.internal.**
-dontnote com.google.android.gms.*.internal.**

# Not used in Android, but in Shared
-dontwarn net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedKmlOut

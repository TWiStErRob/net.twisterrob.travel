### -- BLT/proguard.pro -- ###

# Debugging helpers
#-dontobfuscate
#-dontoptimize
#-optimizationpasses 2

# See res/menu/main.xml / stations_list.xml and b.android.com/170471
-keep class android.support.v7.widget.SearchView { <init>(...); }

# https://issuetracker.google.com/issues/312677256
-dontwarn io.grpc.internal.DnsNameResolverProvider
-dontwarn io.grpc.internal.PickFirstLoadBalancerProvider

# Not used in Android, but in Shared
-dontwarn net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedKmlOut

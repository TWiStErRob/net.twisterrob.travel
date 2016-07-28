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

# FIXME StationListActivity uses Filter.Delayer which is @hide.
# Redefining the class makes it visible and therefore implementable.
-dontnote android.widget.Filter*
-dontwarn android.widget.Filter*
# Pretend that the library-classes are used as is (i.e. without shrinking)
# for searchView.setSearchableInfo(info);
# > SearchView.updateSearchAutoComplete: setAdapter(SuggestionsAdapter)
# > AutoCompleteTextView.setAdapter: ((Filterable) mAdapter).getFilter(); 
# java.lang.AbstractMethodError: abstract method "android.widget.Filter android.widget.Filterable.getFilter()"
# for filter.setDelayer(this);
# java.lang.NoSuchMethodError: No virtual method a(Landroid/widget/Filter$Delayer;)V in class Landroid/widget/Filter;
# or its super classes (declaration of 'android.widget.Filter' appears in /system/framework/framework.jar)
# for filter.filter(query, this);
# java.lang.NoSuchMethodError: No virtual method filter(Ljava/lang/CharSequence;Landroid/widget/Filter$a;)V in class Landroid/widget/Filter;
# or its super classes (declaration of 'android.widget.Filter' appears in /system/framework/framework.jar)
-keep class android.widget.Filter* { *; }

# Not used in Android, but in Shared
-dontwarn net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedKmlOut

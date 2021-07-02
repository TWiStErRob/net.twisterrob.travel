package net.twisterrob.android.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

import androidx.annotation.IntDef;

import com.google.android.gms.location.places.Place;

import net.twisterrob.java.annotations.DebugHelper;

@IntDef(value = {
		Place.TYPE_OTHER,
		Place.TYPE_ACCOUNTING,
		Place.TYPE_AIRPORT,
		Place.TYPE_AMUSEMENT_PARK,
		Place.TYPE_AQUARIUM,
		Place.TYPE_ART_GALLERY,
		Place.TYPE_ATM,
		Place.TYPE_BAKERY,
		Place.TYPE_BANK,
		Place.TYPE_BAR,
		Place.TYPE_BEAUTY_SALON,
		Place.TYPE_BICYCLE_STORE,
		Place.TYPE_BOOK_STORE,
		Place.TYPE_BOWLING_ALLEY,
		Place.TYPE_BUS_STATION,
		Place.TYPE_CAFE,
		Place.TYPE_CAMPGROUND,
		Place.TYPE_CAR_DEALER,
		Place.TYPE_CAR_RENTAL,
		Place.TYPE_CAR_REPAIR,
		Place.TYPE_CAR_WASH,
		Place.TYPE_CASINO,
		Place.TYPE_CEMETERY,
		Place.TYPE_CHURCH,
		Place.TYPE_CITY_HALL,
		Place.TYPE_CLOTHING_STORE,
		Place.TYPE_CONVENIENCE_STORE,
		Place.TYPE_COURTHOUSE,
		Place.TYPE_DENTIST,
		Place.TYPE_DEPARTMENT_STORE,
		Place.TYPE_DOCTOR,
		Place.TYPE_ELECTRICIAN,
		Place.TYPE_ELECTRONICS_STORE,
		Place.TYPE_EMBASSY,
		Place.TYPE_ESTABLISHMENT,
		Place.TYPE_FINANCE,
		Place.TYPE_FIRE_STATION,
		Place.TYPE_FLORIST,
		Place.TYPE_FOOD,
		Place.TYPE_FUNERAL_HOME,
		Place.TYPE_FURNITURE_STORE,
		Place.TYPE_GAS_STATION,
		Place.TYPE_GENERAL_CONTRACTOR,
		Place.TYPE_GROCERY_OR_SUPERMARKET,
		Place.TYPE_GYM,
		Place.TYPE_HAIR_CARE,
		Place.TYPE_HARDWARE_STORE,
		Place.TYPE_HEALTH,
		Place.TYPE_HINDU_TEMPLE,
		Place.TYPE_HOME_GOODS_STORE,
		Place.TYPE_HOSPITAL,
		Place.TYPE_INSURANCE_AGENCY,
		Place.TYPE_JEWELRY_STORE,
		Place.TYPE_LAUNDRY,
		Place.TYPE_LAWYER,
		Place.TYPE_LIBRARY,
		Place.TYPE_LIQUOR_STORE,
		Place.TYPE_LOCAL_GOVERNMENT_OFFICE,
		Place.TYPE_LOCKSMITH,
		Place.TYPE_LODGING,
		Place.TYPE_MEAL_DELIVERY,
		Place.TYPE_MEAL_TAKEAWAY,
		Place.TYPE_MOSQUE,
		Place.TYPE_MOVIE_RENTAL,
		Place.TYPE_MOVIE_THEATER,
		Place.TYPE_MOVING_COMPANY,
		Place.TYPE_MUSEUM,
		Place.TYPE_NIGHT_CLUB,
		Place.TYPE_PAINTER,
		Place.TYPE_PARK,
		Place.TYPE_PARKING,
		Place.TYPE_PET_STORE,
		Place.TYPE_PHARMACY,
		Place.TYPE_PHYSIOTHERAPIST,
		Place.TYPE_PLACE_OF_WORSHIP,
		Place.TYPE_PLUMBER,
		Place.TYPE_POLICE,
		Place.TYPE_POST_OFFICE,
		Place.TYPE_REAL_ESTATE_AGENCY,
		Place.TYPE_RESTAURANT,
		Place.TYPE_ROOFING_CONTRACTOR,
		Place.TYPE_RV_PARK,
		Place.TYPE_SCHOOL,
		Place.TYPE_SHOE_STORE,
		Place.TYPE_SHOPPING_MALL,
		Place.TYPE_SPA,
		Place.TYPE_STADIUM,
		Place.TYPE_STORAGE,
		Place.TYPE_STORE,
		Place.TYPE_SUBWAY_STATION,
		Place.TYPE_SYNAGOGUE,
		Place.TYPE_TAXI_STAND,
		Place.TYPE_TRAIN_STATION,
		Place.TYPE_TRAVEL_AGENCY,
		Place.TYPE_UNIVERSITY,
		Place.TYPE_VETERINARY_CARE,
		Place.TYPE_ZOO,
		Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_1,
		Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_2,
		Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_3,
		Place.TYPE_COLLOQUIAL_AREA,
		Place.TYPE_COUNTRY,
		Place.TYPE_FLOOR,
		Place.TYPE_GEOCODE,
		Place.TYPE_INTERSECTION,
		Place.TYPE_LOCALITY,
		Place.TYPE_NATURAL_FEATURE,
		Place.TYPE_NEIGHBORHOOD,
		Place.TYPE_POLITICAL,
		Place.TYPE_POINT_OF_INTEREST,
		Place.TYPE_POST_BOX,
		Place.TYPE_POSTAL_CODE,
		Place.TYPE_POSTAL_CODE_PREFIX,
		Place.TYPE_POSTAL_TOWN,
		Place.TYPE_PREMISE,
		Place.TYPE_ROOM,
		Place.TYPE_ROUTE,
		Place.TYPE_STREET_ADDRESS,
		Place.TYPE_SUBLOCALITY,
		Place.TYPE_SUBLOCALITY_LEVEL_1,
		Place.TYPE_SUBLOCALITY_LEVEL_2,
		Place.TYPE_SUBLOCALITY_LEVEL_3,
		Place.TYPE_SUBLOCALITY_LEVEL_4,
		Place.TYPE_SUBLOCALITY_LEVEL_5,
		Place.TYPE_SUBPREMISE,
		Place.TYPE_SYNTHETIC_GEOCODE,
		Place.TYPE_TRANSIT_STATION,
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface PlaceType {
	class Converter {
		@DebugHelper
		public static String toString(@PlaceType int type) {
			switch (type) {
				case Place.TYPE_OTHER:
					return "OTHER";
				case Place.TYPE_ACCOUNTING:
					return "ACCOUNTING";
				case Place.TYPE_AIRPORT:
					return "AIRPORT";
				case Place.TYPE_AMUSEMENT_PARK:
					return "AMUSEMENT_PARK";
				case Place.TYPE_AQUARIUM:
					return "AQUARIUM";
				case Place.TYPE_ART_GALLERY:
					return "ART_GALLERY";
				case Place.TYPE_ATM:
					return "ATM";
				case Place.TYPE_BAKERY:
					return "BAKERY";
				case Place.TYPE_BANK:
					return "BANK";
				case Place.TYPE_BAR:
					return "BAR";
				case Place.TYPE_BEAUTY_SALON:
					return "BEAUTY_SALON";
				case Place.TYPE_BICYCLE_STORE:
					return "BICYCLE_STORE";
				case Place.TYPE_BOOK_STORE:
					return "BOOK_STORE";
				case Place.TYPE_BOWLING_ALLEY:
					return "BOWLING_ALLEY";
				case Place.TYPE_BUS_STATION:
					return "BUS_STATION";
				case Place.TYPE_CAFE:
					return "CAFE";
				case Place.TYPE_CAMPGROUND:
					return "CAMPGROUND";
				case Place.TYPE_CAR_DEALER:
					return "CAR_DEALER";
				case Place.TYPE_CAR_RENTAL:
					return "CAR_RENTAL";
				case Place.TYPE_CAR_REPAIR:
					return "CAR_REPAIR";
				case Place.TYPE_CAR_WASH:
					return "CAR_WASH";
				case Place.TYPE_CASINO:
					return "CASINO";
				case Place.TYPE_CEMETERY:
					return "CEMETERY";
				case Place.TYPE_CHURCH:
					return "CHURCH";
				case Place.TYPE_CITY_HALL:
					return "CITY_HALL";
				case Place.TYPE_CLOTHING_STORE:
					return "CLOTHING_STORE";
				case Place.TYPE_CONVENIENCE_STORE:
					return "CONVENIENCE_STORE";
				case Place.TYPE_COURTHOUSE:
					return "COURTHOUSE";
				case Place.TYPE_DENTIST:
					return "DENTIST";
				case Place.TYPE_DEPARTMENT_STORE:
					return "DEPARTMENT_STORE";
				case Place.TYPE_DOCTOR:
					return "DOCTOR";
				case Place.TYPE_ELECTRICIAN:
					return "ELECTRICIAN";
				case Place.TYPE_ELECTRONICS_STORE:
					return "ELECTRONICS_STORE";
				case Place.TYPE_EMBASSY:
					return "EMBASSY";
				case Place.TYPE_ESTABLISHMENT:
					return "ESTABLISHMENT";
				case Place.TYPE_FINANCE:
					return "FINANCE";
				case Place.TYPE_FIRE_STATION:
					return "FIRE_STATION";
				case Place.TYPE_FLORIST:
					return "FLORIST";
				case Place.TYPE_FOOD:
					return "FOOD";
				case Place.TYPE_FUNERAL_HOME:
					return "FUNERAL_HOME";
				case Place.TYPE_FURNITURE_STORE:
					return "FURNITURE_STORE";
				case Place.TYPE_GAS_STATION:
					return "GAS_STATION";
				case Place.TYPE_GENERAL_CONTRACTOR:
					return "GENERAL_CONTRACTOR";
				case Place.TYPE_GROCERY_OR_SUPERMARKET:
					return "GROCERY_OR_SUPERMARKET";
				case Place.TYPE_GYM:
					return "GYM";
				case Place.TYPE_HAIR_CARE:
					return "HAIR_CARE";
				case Place.TYPE_HARDWARE_STORE:
					return "HARDWARE_STORE";
				case Place.TYPE_HEALTH:
					return "HEALTH";
				case Place.TYPE_HINDU_TEMPLE:
					return "HINDU_TEMPLE";
				case Place.TYPE_HOME_GOODS_STORE:
					return "HOME_GOODS_STORE";
				case Place.TYPE_HOSPITAL:
					return "HOSPITAL";
				case Place.TYPE_INSURANCE_AGENCY:
					return "INSURANCE_AGENCY";
				case Place.TYPE_JEWELRY_STORE:
					return "JEWELRY_STORE";
				case Place.TYPE_LAUNDRY:
					return "LAUNDRY";
				case Place.TYPE_LAWYER:
					return "LAWYER";
				case Place.TYPE_LIBRARY:
					return "LIBRARY";
				case Place.TYPE_LIQUOR_STORE:
					return "LIQUOR_STORE";
				case Place.TYPE_LOCAL_GOVERNMENT_OFFICE:
					return "LOCAL_GOVERNMENT_OFFICE";
				case Place.TYPE_LOCKSMITH:
					return "LOCKSMITH";
				case Place.TYPE_LODGING:
					return "LODGING";
				case Place.TYPE_MEAL_DELIVERY:
					return "MEAL_DELIVERY";
				case Place.TYPE_MEAL_TAKEAWAY:
					return "MEAL_TAKEAWAY";
				case Place.TYPE_MOSQUE:
					return "MOSQUE";
				case Place.TYPE_MOVIE_RENTAL:
					return "MOVIE_RENTAL";
				case Place.TYPE_MOVIE_THEATER:
					return "MOVIE_THEATER";
				case Place.TYPE_MOVING_COMPANY:
					return "MOVING_COMPANY";
				case Place.TYPE_MUSEUM:
					return "MUSEUM";
				case Place.TYPE_NIGHT_CLUB:
					return "NIGHT_CLUB";
				case Place.TYPE_PAINTER:
					return "PAINTER";
				case Place.TYPE_PARK:
					return "PARK";
				case Place.TYPE_PARKING:
					return "PARKING";
				case Place.TYPE_PET_STORE:
					return "PET_STORE";
				case Place.TYPE_PHARMACY:
					return "PHARMACY";
				case Place.TYPE_PHYSIOTHERAPIST:
					return "PHYSIOTHERAPIST";
				case Place.TYPE_PLACE_OF_WORSHIP:
					return "PLACE_OF_WORSHIP";
				case Place.TYPE_PLUMBER:
					return "PLUMBER";
				case Place.TYPE_POLICE:
					return "POLICE";
				case Place.TYPE_POST_OFFICE:
					return "POST_OFFICE";
				case Place.TYPE_REAL_ESTATE_AGENCY:
					return "REAL_ESTATE_AGENCY";
				case Place.TYPE_RESTAURANT:
					return "RESTAURANT";
				case Place.TYPE_ROOFING_CONTRACTOR:
					return "ROOFING_CONTRACTOR";
				case Place.TYPE_RV_PARK:
					return "RV_PARK";
				case Place.TYPE_SCHOOL:
					return "SCHOOL";
				case Place.TYPE_SHOE_STORE:
					return "SHOE_STORE";
				case Place.TYPE_SHOPPING_MALL:
					return "SHOPPING_MALL";
				case Place.TYPE_SPA:
					return "SPA";
				case Place.TYPE_STADIUM:
					return "STADIUM";
				case Place.TYPE_STORAGE:
					return "STORAGE";
				case Place.TYPE_STORE:
					return "STORE";
				case Place.TYPE_SUBWAY_STATION:
					return "SUBWAY_STATION";
				case Place.TYPE_SYNAGOGUE:
					return "SYNAGOGUE";
				case Place.TYPE_TAXI_STAND:
					return "TAXI_STAND";
				case Place.TYPE_TRAIN_STATION:
					return "TRAIN_STATION";
				case Place.TYPE_TRAVEL_AGENCY:
					return "TRAVEL_AGENCY";
				case Place.TYPE_UNIVERSITY:
					return "UNIVERSITY";
				case Place.TYPE_VETERINARY_CARE:
					return "VETERINARY_CARE";
				case Place.TYPE_ZOO:
					return "ZOO";
				case Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_1:
					return "ADMINISTRATIVE_AREA_LEVEL_1";
				case Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_2:
					return "ADMINISTRATIVE_AREA_LEVEL_2";
				case Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_3:
					return "ADMINISTRATIVE_AREA_LEVEL_3";
				case Place.TYPE_COLLOQUIAL_AREA:
					return "COLLOQUIAL_AREA";
				case Place.TYPE_COUNTRY:
					return "COUNTRY";
				case Place.TYPE_FLOOR:
					return "FLOOR";
				case Place.TYPE_GEOCODE:
					return "GEOCODE";
				case Place.TYPE_INTERSECTION:
					return "INTERSECTION";
				case Place.TYPE_LOCALITY:
					return "LOCALITY";
				case Place.TYPE_NATURAL_FEATURE:
					return "NATURAL_FEATURE";
				case Place.TYPE_NEIGHBORHOOD:
					return "NEIGHBORHOOD";
				case Place.TYPE_POLITICAL:
					return "POLITICAL";
				case Place.TYPE_POINT_OF_INTEREST:
					return "POINT_OF_INTEREST";
				case Place.TYPE_POST_BOX:
					return "POST_BOX";
				case Place.TYPE_POSTAL_CODE:
					return "POSTAL_CODE";
				case Place.TYPE_POSTAL_CODE_PREFIX:
					return "POSTAL_CODE_PREFIX";
				case Place.TYPE_POSTAL_TOWN:
					return "POSTAL_TOWN";
				case Place.TYPE_PREMISE:
					return "PREMISE";
				case Place.TYPE_ROOM:
					return "ROOM";
				case Place.TYPE_ROUTE:
					return "ROUTE";
				case Place.TYPE_STREET_ADDRESS:
					return "STREET_ADDRESS";
				case Place.TYPE_SUBLOCALITY:
					return "SUBLOCALITY";
				case Place.TYPE_SUBLOCALITY_LEVEL_1:
					return "SUBLOCALITY_LEVEL_1";
				case Place.TYPE_SUBLOCALITY_LEVEL_2:
					return "SUBLOCALITY_LEVEL_2";
				case Place.TYPE_SUBLOCALITY_LEVEL_3:
					return "SUBLOCALITY_LEVEL_3";
				case Place.TYPE_SUBLOCALITY_LEVEL_4:
					return "SUBLOCALITY_LEVEL_4";
				case Place.TYPE_SUBLOCALITY_LEVEL_5:
					return "SUBLOCALITY_LEVEL_5";
				case Place.TYPE_SUBPREMISE:
					return "SUBPREMISE";
				case Place.TYPE_SYNTHETIC_GEOCODE:
					return "SYNTHETIC_GEOCODE";
				case Place.TYPE_TRANSIT_STATION:
					return "TRANSIT_STATION";
			}
			return "placeType::" + type;
		}
	}
}

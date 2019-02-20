package mark1.gmt.rk_rabbitt.gmt_driver.Utils;

/**
 * Created by Rabbitt on 19,February,2019
 */
public class Config {
        public static final String STATUS_UPDATE = "http://192.168.43.252:8080/GotMyTrip/driverStatus.php";
        public static final String SHARED_PREF = "TOKEN_PREFS   ";
        public static final String TOKEN_UPDATE = "http://192.168.43.252:8080/GotMyTrip/tokenUpdate.php";
        public static final String USER_LOGIN = "http://192.168.43.252:8080/GotMyTrip/driverLogin.php";
        public static final String USER_PREFS = "USER_DETAILS";

        static final String DISTANCE_CALC = "http://192.168.43.252:8080/GotMyTrip/distanceCalculator.php";
        static final String CUSTOMER_CITY_BOOK = "http://192.168.43.252:8080/GotMyTrip/cityBooking.php";
        static final String CUSTOMER_RENTAL_BOOK = "http://192.168.43.252:8080/GotMyTrip/rentalBooking.php";
        static final String CUSTOMER_OUTSTATION_BOOK = "http://192.168.43.252:8080/IntelGate/outstationBooking.php";
        static final String PRECHECK_AVAIL = "http://192.168.43.252:8080/IntelGate/preCheckingPayment.php";
}

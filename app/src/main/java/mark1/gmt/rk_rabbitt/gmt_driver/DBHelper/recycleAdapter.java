package mark1.gmt.rk_rabbitt.gmt_driver.DBHelper;

public class recycleAdapter{
    public String timeat;
    public String travel_type;
    public String book_id;
    public String origin, destination;
    public String v_type;


    public String package_id;
//
//    public recycleAdapter(String timeat, String travel_type, String book_id, String origin, String destination, String v_type) {
//        this.timeat = timeat;
//        this.travel_type = travel_type;
//        this.book_id = book_id;
//        this.origin = origin;
//        this.destination = destination;
//        this.v_type = v_type;
//    }

    public String getV_type() {
        return v_type;
    }

    public String getTimeat() {
        return timeat;
    }

    public String getTravel_type() {
        return travel_type;
    }

    public String getBook_id() {
        return book_id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setTimeat(String timeat) {
        this.timeat = timeat;
    }

    public void setTravel_type(String travel_type) {
        this.travel_type = travel_type;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    void setOrigin(String origin) {
        this.origin = origin;
    }

    void setDestination(String destination) {
        this.destination = destination;
    }

    public void setV_type(String v_type) {
        this.v_type = v_type;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getPackage_id() {

        return package_id;
    }
}

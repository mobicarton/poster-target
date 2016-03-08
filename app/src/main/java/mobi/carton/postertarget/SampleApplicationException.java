package mobi.carton.postertarget;


// Used to send back to the activity any error during vuforia processes
public class SampleApplicationException extends Exception {


    private static final long serialVersionUID = 2L;

    private String mString = "";


    public SampleApplicationException(String description) {
        super(description);
        mString = description;
    }


    public String getString() {
        return mString;
    }
}

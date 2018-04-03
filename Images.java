package asatkeeva.finalfotoswish;

import java.util.ArrayList;

/**
 * Created by anara.satkeeva on 3/15/2018.
 */

public class Images {
    String str_folder;
    ArrayList<String> imagepath;

    public String get_folder() {
        return str_folder;
    }

    public void set_folder(String str_folder) {
        this.str_folder = str_folder;
    }

    public ArrayList<String> get_imagePath() {
        return imagepath;
    }

    public void set_imagePath(ArrayList<String> imagepath) {
        this.imagepath = imagepath;
    }
}

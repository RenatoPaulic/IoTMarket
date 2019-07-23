package help;

/**
 * Container class that stores
 * area coordinates
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AreaDots {

    private int min_x;
    private int max_x;
    private int min_y;
    private int max_y;

    public AreaDots(int min_x, int max_x, int min_y, int max_y){

        this.min_x = min_x;
        this.max_x = max_x;
        this.min_y = min_y;
        this.max_y = max_y;

    }

    public int getMin_x() { return min_x; }
    public void setMin_x(int min_x) {
        this.min_x = min_x;
    }
    public int getMax_x() {
        return max_x;
    }
    public void setMax_x(int max_x) {
        this.max_x = max_x;
    }
    public int getMin_y() {
        return min_y;
    }
    public void setMin_y(int min_y) {
        this.min_y = min_y;
    }
    public int getMax_y() {
        return max_y;
    }
    public void setMax_y(int max_y) {
        this.max_y = max_y;
    }

    @Override
    public String toString (){
        return "Area: " + min_x + " " + max_x + " " + min_y + " " + max_y;
    }

}

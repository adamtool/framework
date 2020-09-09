package uniolunisaar.adam.ds;

/**
 *
 * @author Manuel Gieseking
 */
public class BoundingBox {

    private double top;
    private double bottom;
    private double left;
    private double right;

    public BoundingBox() {
    }

    public BoundingBox(double top, double bottom, double left, double right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public double getBottom() {
        return bottom;
    }

    public void setBottom(double bottom) {
        this.bottom = bottom;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
    }

    public double getWidth() {
        return right - left;
    }

    public double getHeight() {
        return bottom - top;
    }

}

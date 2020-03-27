package uniolunisaar.adam.ds.circuits;

/**
 *
 * @author Manuel Gieseking
 */
public class Gate {

    private String in1;
    private String in2;
    private final String out;
    private int idx = -1;

    public Gate(String out, String in1, String in2) {
        this.in1 = in1;
        this.in2 = in2;
        this.out = out;
    }

    public String getIn1() {
        return in1;
    }

    public String getIn2() {
        return in2;
    }

    public String getOut() {
        return out;
    }

    public void setIn1(String in1) {
        this.in1 = in1;
    }

    public void setIn2(String in2) {
        this.in2 = in2;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    @Override
    public String toString() {
        return "Gate{" + "in1=" + in1 + ", in2=" + in2 + ", out=" + out + '}';
    }

}

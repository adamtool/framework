package uniolunisaar.adam.generators.pnwt;

import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 *
 * @author Manuel Gieseking
 */
public class ToyExamples {

    public static PetriNetWithTransits createFirstExample(boolean looping) {
        PetriNetWithTransits net = new PetriNetWithTransits("firstExample_" + looping);
        Place in = net.createPlace("in");
        in.setInitialToken(1);
        net.setInitialTransit(in);
        Place out = net.createPlace("out");
        out.setInitialToken(1);
        Transition t = net.createTransition("t");
        net.createFlow(t, in);
        net.createFlow(in, t);
        net.createFlow(t, out);
        net.createFlow(out, t);
        if (looping) {
            net.createTransit(in, t, in, out);
        } else {
            net.createTransit(in, t, out);
        }
        net.createTransit(out, t, out);
        return net;
    }

    public static PetriNetWithTransits createFirstExampleExtended(boolean looping) {
        PetriNetWithTransits net = new PetriNetWithTransits("firstExampleExtended_" + looping);
        Place in = net.createPlace("in");
        in.setInitialToken(1);
        net.setInitialTransit(in);
        Place mid = net.createPlace("mid");
        mid.setInitialToken(1);
        Transition t = net.createTransition("ta");
        net.setWeakFair(t);
        net.createFlow(in, t);
        net.createFlow(t, in);
        net.createFlow(t, mid);
        net.createFlow(mid, t);
        net.createTransit(mid, t, mid);

        Place out = net.createPlace("out");
        out.setInitialToken(1);
        Transition t1 = net.createTransition("tb");
        net.setWeakFair(t1);
        net.createFlow(t1, mid);
        net.createFlow(mid, t1);
        net.createFlow(t1, out);
        net.createFlow(out, t1);
        net.createTransit(out, t1, out);

        if (looping) {
            net.createTransit(mid, t1, mid, out);
            net.createTransit(in, t, in, mid);
        } else {
            net.createTransit(mid, t1, out);
            net.createTransit(in, t, mid);
        }
        return net;
//        
//        false code
// PetriGame net = createFirstExampleExtended();
//        net.setName(net.getName() + "_positiv");
//        net.removeTransit("ta", "in");
//        net.removeTransit("tb", "mid");
    }

    public static PetriNetWithTransits createIntroductoryExample() {
        PetriNetWithTransits net = new PetriNetWithTransits("introduction");
        Place a = net.createPlace("a");
        a.setInitialToken(1);
        net.setInitialTransit(a);
        Place b = net.createPlace("B");
        b.setInitialToken(1);
        net.setInitialTransit(b);
        Place c = net.createPlace("C");
        c.setInitialToken(1);
        Place d = net.createPlace("D");
        Place e = net.createPlace("E");
        Place f = net.createPlace("F");
        Transition t1 = net.createTransition("o1");
        Transition t2 = net.createTransition("o2");
        net.createFlow(a, t1);
        net.createFlow(b, t1);
        net.createFlow(t1, d);
        net.createFlow(c, t2);
        net.createFlow(d, t2);
        net.createFlow(t2, e);
        net.createFlow(t2, f);
        net.createFlow(t2, b);
        net.createTransit(a, t1, d);
        net.createTransit(b, t1, d);
        net.createTransit(d, t2, e, b);
        net.createInitialTransit(t2, f);
        return net;
    }
}

package uniolunisaar.adam.generators.pnwt;

import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 *
 * @author Manuel Gieseking
 */
public class SmartFactory {

    /**
     * Creates a smart factory which can create two products (a work piece with
     * drilled holes, another with T-slots) The machines are milling, drilling
     * (one for holes, one for T-slots), deburring, and two validation machines.
     * It is possible to activate both validation machines or only one at a
     * time.
     *
     * @param withUpdate
     * @return
     */
    public static PetriNetWithTransits createMillingDrillingDeburringValidationExample(boolean withUpdate) {
        PetriNetWithTransits net = new PetriNetWithTransits("SmartFactory");
        // scanning process
        Place scan = net.createPlace("scan");
        scan.setInitialToken(1);
        // create machines
        addMachine("m", net);
        addMachine("dH", net);
        addMachine("dT", net);
        addMachine("db", net);
        addMachine("vA", net);
        if (withUpdate) {
            addMachine("vB", net);
        }
        // create types
        String type = "H";
        for (int i = 0; i < 2; i++) {
            Place start_milling = net.createPlace("sm_" + i);
            start_milling.setInitialToken(1);
            // scanning
            Transition tscan = net.createTransition();
            net.createFlow(scan, tscan);
            net.createFlow(start_milling, tscan);
            net.createFlow(tscan, scan);
            net.createFlow(tscan, start_milling);
            net.createTransit(start_milling, tscan, start_milling);
            net.createInitialTransit(tscan, start_milling);
            // milling
            Place work_milling = net.createPlace("wm_" + i);
            Place start_drilling = net.createPlace("sd_" + i);
            start_drilling.setInitialToken(1);
            connectMachine("m", start_milling, work_milling, start_drilling, net, i);
            // drilling
            Place work_drilling = net.createPlace("wd_" + i);
            Place start_deburring = net.createPlace("sdb_" + i);
            start_deburring.setInitialToken(1);
            connectMachine("d" + type, start_drilling, work_drilling, start_deburring, net, i);
            // deburring
            Place work_deburring = net.createPlace("wdb_" + i);
            Place start_validating = net.createPlace("sv_" + i);
            start_validating.setInitialToken(1);
            connectMachine("db", start_deburring, work_deburring, start_validating, net, i);
            // validating
            Place work_validating = net.createPlace("wvA_" + i);
            Place finish_validating = net.createPlace("fv_" + i);
            finish_validating.setInitialToken(1);
            connectMachine("vA", start_validating, work_validating, finish_validating, net, i);
            if (withUpdate) {
                Place work_validatingB = net.createPlace("wvB_" + i);
                connectMachine("vB", start_validating, work_validatingB, finish_validating, net, i);
            }
            type = "T";
        }
        if (withUpdate) {
            Place update = net.createPlace("update");
            // Validation A
            Place updateA = net.createPlace("upA");
            Place vA = net.getPlace("vA_i");
            Transition t1 = net.createTransition();
            net.createFlow(vA, t1);
            net.createFlow(update, t1);
            net.createFlow(t1, updateA);
            Transition t2 = net.createTransition();
            net.createFlow(updateA, t2);
            net.createFlow(t2, update);
            net.createFlow(t2, vA);
            // Validation A
            Place updateB = net.createPlace("upB");
            updateB.setInitialToken(1);
            Place vB = net.getPlace("vB_i");
            t1 = net.createTransition();
            net.createFlow(vB, t1);
            net.createFlow(update, t1);
            net.createFlow(t1, updateB);
            t2 = net.createTransition();
            net.createFlow(updateB, t2);
            net.createFlow(t2, update);
            net.createFlow(t2, vB);
        }
        return net;
    }

    private static void addMachine(String prefix, PetriNetWithTransits pn) {
        Place idle = pn.createPlace(prefix + "_i");
        idle.setInitialToken(1);
        pn.createPlace(prefix + "_w");
    }

    private static void connectMachine(String prefix, Place start, Place working, Place end, PetriNetWithTransits pn, int type) {
        Place idle = pn.getPlace(prefix + "_i");
        Place work = pn.getPlace(prefix + "_w");
        Transition w = pn.createTransition(prefix + "_w_" + type);
        pn.setStrongFair(w);
        Transition f = pn.createTransition(prefix + "_f_" + type);
        pn.setStrongFair(f);
        // flows
        pn.createFlow(start, w);
        pn.createFlow(idle, w);
        pn.createFlow(w, start);
        pn.createFlow(w, working);
        pn.createFlow(w, work);
        pn.createFlow(work, f);
        pn.createFlow(end, f);
        pn.createFlow(working, f);
        pn.createFlow(f, idle);
        pn.createFlow(f, end);
        // transits
        pn.createTransit(start, w, work);
        pn.createTransit(work, f, end);
    }
    
}

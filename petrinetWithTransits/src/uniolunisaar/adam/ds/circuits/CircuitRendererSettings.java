package uniolunisaar.adam.ds.circuits;

/**
 *
 * @author Manuel Gieseking
 */
public class CircuitRendererSettings {

    public enum TransitionSemantics {
        INGOING,
        OUTGOING
    }

    public enum TransitionEncoding {
        EXPLICIT,
        LOGARITHMIC
    }

    public enum AtomicPropositions {
        PLACES,
        FIREABILITY,
        PLACES_AND_TRANSITIONS
    }

    private TransitionSemantics semantics;
    private TransitionEncoding encoding;
    private AtomicPropositions atoms;
    private boolean maxInterleaving = true;

    public CircuitRendererSettings(TransitionSemantics semantics, TransitionEncoding encoding, AtomicPropositions atoms) {
        this.semantics = semantics;
        this.encoding = encoding;
        this.atoms = atoms;
    }

    public TransitionSemantics getSemantics() {
        return semantics;
    }

    public TransitionEncoding getEncoding() {
        return encoding;
    }

    public AtomicPropositions getAtoms() {
        return atoms;
    }

    public boolean isMaxInterleaving() {
        return maxInterleaving;
    }

    /**
     * Attention: don't use this method, this flag is automatically set by the
     * AdamCircuitMCSettings!
     *
     * @param maxInterleaving
     */
    public void setMaxInterleaving(boolean maxInterleaving) {
        this.maxInterleaving = maxInterleaving;
    }

    public void setSemantics(TransitionSemantics semantics) {
        this.semantics = semantics;
    }

    public void setEncoding(TransitionEncoding encoding) {
        this.encoding = encoding;
    }

    public void setAtoms(AtomicPropositions atoms) {
        this.atoms = atoms;
    }

}

package uniolunisaar.adam.ds.abta;

import uniolunisaar.adam.ds.abta.posbooleanformula.IPositiveBooleanFormula;

/**
 *
 * @author Manuel Gieseking
 * @param <Sigma>
 */
public class TreeEdge<Sigma> {

    private final TreeState s;
    private final Sigma sigma;
    private final int degree;
    private final IPositiveBooleanFormula successor;

    TreeEdge(TreeState s, Sigma sigma, int degree, IPositiveBooleanFormula successor) {
        this.s = s;
        this.sigma = sigma;
        this.degree = degree;
        this.successor = successor;
    }

    public TreeState getS() {
        return s;
    }

    public Sigma getSigma() {
        return sigma;
    }

    public int getDegree() {
        return degree;
    }

    public IPositiveBooleanFormula getSuccessor() {
        return successor;
    }

}

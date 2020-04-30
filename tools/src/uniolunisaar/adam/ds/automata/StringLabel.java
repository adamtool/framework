package uniolunisaar.adam.ds.automata;

import java.util.Objects;

/**
 *
 * @author Manuel Gieseking
 */
public class StringLabel implements ILabel {

    private final String text;

    public StringLabel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.text.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StringLabel other = (StringLabel) obj;
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return text;
    }

}

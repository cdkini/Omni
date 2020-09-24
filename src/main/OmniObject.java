package src.main;

import java.io.Serializable;

public abstract class OmniObject implements Serializable {
    protected String hash;

    protected void setHash(Object... vals) {
        this.hash = Utils.sha1(vals);
    }
}

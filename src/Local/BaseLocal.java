package Local;

import partial.AbstractPartial;

public abstract class BaseLocal extends AbstractPartial {
    protected BaseLocal() {
        super();
    }

    public abstract long getLocalMillis();
}

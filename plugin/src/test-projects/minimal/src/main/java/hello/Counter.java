package hello;

import rife.engine.Context;
import rife.engine.Element;

public class Counter implements Element {
    public void process(Context c) {
        c.pause();
    }
}

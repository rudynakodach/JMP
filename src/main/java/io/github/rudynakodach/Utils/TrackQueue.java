package io.github.rudynakodach.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public final class TrackQueue<T> extends LinkedBlockingQueue<T> {
    public T current;

    @Override
    public T poll() {
        T object = super.poll();
        current = object;
        return object;
    }

    public List<T> getQueue(boolean includeCurrent) {
        List<T> list = new ArrayList<>();
        if(current != null && includeCurrent) {
            list.add(current);
        }
        list.addAll(this);
        return list;
    }

    public void clear() {
        current = null;
        while (!this.isEmpty()) {
            this.remove();
        }
    }
}


package businessLogic;

import java.util.List;
import java.util.NoSuchElementException;

public class CityIterator implements ExtendedIterator<String> {
    private List<String> cities;
    private int position;

    public CityIterator(List<String> cities) {
        this.cities = cities;
        this.position = 0; // Comienza en el primer elemento
    }

    @Override
    public boolean hasNext() {
        return position < cities.size();
    }

    @Override
    public String next() {
        if (!hasNext()) throw new NoSuchElementException();
        return cities.get(position++);
    }

    @Override
    public String previous() {
        if (!hasPrevious()) throw new NoSuchElementException();
        return cities.get(--position);
    }

    @Override
    public boolean hasPrevious() {
        return position > 0;
    }

    @Override
    public void goFirst() {
        position = 0;
    }

    @Override
    public void goLast() {
        position = cities.size();
    }
}

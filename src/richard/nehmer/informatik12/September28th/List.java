package richard.nehmer.informatik12.September28th;

public class List {
    private Physichists[] elements;
    private int size;

    public List() {
        this(100);
    }

    public List(int maximum_length) {
        elements = new Physichists[maximum_length];
        size = 0;
    }

    public void addToList(Physichists element) {
        if (size < elements.length) {
            elements[size] = element;
            size++;
        }
        // If full, silently ignore (as per your original design).
    }

    public void removeFromList(int idx) {
        // Valid index range: 0 <= idx < size
        if (idx < 0 || idx >= size) {
            return;
        }

        // Shift left to remove element at idx
        for (int i = idx; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        size--;
        elements[size] = null; // clear last reference
    }

    public void removeFromList(Physichists element) {
        int idx = getIndexInList(element);
        if (idx >= 0) {
            removeFromList(idx);
        }
    }

    public int getIndexInList(Physichists element) {
        for (int i = 0; i < size; i++) {
            if (elements[i] != null && elements[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public Physichists getElement(Physichists element) {
        int idx = getIndexInList(element);
        return (idx >= 0) ? elements[idx] : null;
    }

    public void printToLine() {
        System.out.println("The List contains " + this.size + " of " + this.elements.length + " possible Elements");
        System.out.println("Saved are:");
        for (int i = 0; i < size; i++) {
            System.out.println(i + ": " + elements[i].returnInfo());
        }
    }
}
/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import ua.kpi.comsys.test2.NumberList;

/**
 * Кільцевий двонаправлений список
 * Двійкова система числення 
 * Операція алгебраїчного та логічного AND двох чисел
 * Додаткова система: трійкова
 *
 * @author Гармаш Максим Андрійович
 * Група: ІС-31
 * Варіант: 5
 */

public class NumberListImpl implements NumberList {

    private static final int DEFAULT_BASE = 2; // двійкова система
    private static final int ALTERNATIVE_BASE = 3; // трійкова система

    private final int base;
    private Node head;
    private int size;
    private int modCount = 0;

    // Вузол кільцевого двонаправленого списку
    private static class Node {
        byte data;
        Node next;
        Node prev;

        Node(byte data) {
            this.data = data;
        }
    }

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.base = DEFAULT_BASE;
        this.head = null;
        this.size = 0;
    }

    /**
     * Constructor with custom base.
     */
    private NumberListImpl(int base) {
        this.base = base;
        this.head = null;
        this.size = 0;
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                initFromDecimalString(line.trim());
            }
        } catch (Exception e) {}
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        try {
            initFromDecimalString(value);
        } catch (Exception e) {}
    }

    private void initFromDecimalString(String decimalStr) {
        if (decimalStr == null || decimalStr.isEmpty()) return;
        try {
            BigInteger decimalValue = new BigInteger(decimalStr.trim());
            if (decimalValue.compareTo(BigInteger.ZERO) < 0) return;
            
            String inBase = decimalValue.toString(base);
            for (char c : inBase.toCharArray()) {
                add((byte) Character.getNumericValue(c));
            }
        } catch (NumberFormatException e) {}
    }

    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(toDecimalString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 5;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in other scale of notation.
     */
    public NumberListImpl changeScale() {
        BigInteger decimalValue = new BigInteger(toDecimalString());
        NumberListImpl result = new NumberListImpl(ALTERNATIVE_BASE);

        if (decimalValue.equals(BigInteger.ZERO)) {
            result.add((byte)0);
            return result;
        }

        // Перетворення вручну у трійкову систему
        BigInteger baseAlt = BigInteger.valueOf(ALTERNATIVE_BASE);
        while (decimalValue.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = decimalValue.divideAndRemainder(baseAlt);
            // додаємо розряд на початок списку
            result.add(0, divRem[1].byteValue());
            decimalValue = divRem[0];
        }

        return result;  
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of additional operation
     *
     * @return result of additional operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        if (arg == null) throw new IllegalArgumentException("Argument cannot be null");

        // Використовуємо toDecimalString для універсальності операндів
        BigInteger n1 = new BigInteger(this.toDecimalString());
        BigInteger n2 = new BigInteger(((NumberListImpl)arg).toDecimalString());

        BigInteger andResult = n1.and(n2);

        // Створюємо результат. Конструктор за замовчуванням встановить base=2
        NumberListImpl result = new NumberListImpl();
        
        // Використовуємо ініціалізацію через BigInteger, щоб не писати цикли вручну
        String binary = andResult.toString(2); 
        for (char c : binary.toCharArray()) {
            result.add((byte) Character.getNumericValue(c));
        }
        return result;
    }


    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        if (isEmpty()) return "0";

        BigInteger decimal = BigInteger.ZERO;
        BigInteger baseVal = BigInteger.valueOf(base);

        Node curr = head;
        for (int i = 0; i < size; i++) {
            decimal = decimal.multiply(baseVal).add(BigInteger.valueOf(curr.data));
            curr = curr.next;
        }

        return decimal.toString();
    }


    @Override
    public String toString() {
        if (isEmpty()) return "0";

        StringBuilder sb = new StringBuilder();
        Node curr = head;

        for (int i = 0; i < size; i++) {
            sb.append(curr.data);
            curr = curr.next;
        }

        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberList)) return false;

        NumberList other = (NumberList) o;
        
        if (this.size() != other.size()) return false;

        Node curr = this.head;
        for (int i = 0; i < size; i++) {
            if (!Byte.valueOf(curr.data).equals(other.get(i))) return false;
            curr = curr.next;
        }
        return true;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Byte)) return false;
        if (isEmpty()) return false;

        byte target = (Byte) o;
        Node curr = head;

        for (int i = 0; i < size; i++) {
            if (curr.data == target) return true;
            curr = curr.next;
        }
        return false;
    }


    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            private int visited = 0;

            @Override
            public boolean hasNext() {
                return visited < size;
            }

            @Override
            public Byte next() {
                Byte val = current.data;
                current = current.next;
                visited++;
                return val;
            }
        };
    }



    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node curr = head;
        for (int i = 0; i < size; i++) {
            arr[i] = curr.data;
            curr = curr.next;
        }
        return arr;
    }



    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }


    @Override
    public boolean add(Byte e) {
        if (e == null) throw new NullPointerException();
        if (e < 0 || e >= base)
            throw new IllegalArgumentException("Digit out of range");

        Node node = new Node(e);

        if (isEmpty()) {
            head = node;
            head.next = head;
            head.prev = head;
        } else {
            Node tail = head.prev;
            tail.next = node;
            node.prev = tail;
            node.next = head;
            head.prev = node;
        }

        size++;
        modCount++;
        return true;
    }


    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Byte) || isEmpty()) return false;

        byte target = (Byte) o;
        Node curr = head;

        for (int i = 0; i < size; i++) {
            if (curr.data == target) {
                if (size == 1) {
                    head = null;
                } else {
                    curr.prev.next = curr.next;
                    curr.next.prev = curr.prev;
                    if (curr == head) head = curr.next;
                }
                size--;
                modCount++;
                return true;
            }
            curr = curr.next;
        }
        return false;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }


    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        if (c == null || c.isEmpty()) return false;
        for (Byte b : c) add(b);
        return true;
    }


   @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException();

        if (c == null || c.isEmpty()) return false;

        for (Byte b : c) {
            add(index++, b);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        Iterator<Byte> it = iterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove(); // Видаляє саме поточний вузол
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        Iterator<Byte> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
        modCount++;
    }

    @Override
    public Byte get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();

        Node curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.data;
    }

    @Override
    public Byte set(int index, Byte element) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        if (element == null)
            throw new NullPointerException();
        if (element < 0 || element >= base)
            throw new IllegalArgumentException();

        Node curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }

        byte old = curr.data;
        curr.data = element;
        modCount++;
        return old;
    }

    @Override
    public void add(int index, Byte element) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException();
        if (element == null)
            throw new NullPointerException();
        if (element < 0 || element >= base)
            throw new IllegalArgumentException();

        if (index == size) {
            add(element);
            return;
        }

        Node newNode = new Node(element);

        if (index == 0) {
            if (isEmpty()) {
                head = newNode;
                newNode.next = newNode;
                newNode.prev = newNode;
            } else {
                Node tail = head.prev;
                newNode.next = head;
                newNode.prev = tail;
                head.prev = newNode;
                tail.next = newNode;
                head = newNode;
            }
        } else {
            Node curr = head;
            for (int i = 0; i < index; i++) {
                curr = curr.next;
            }
            Node prev = curr.prev;
            prev.next = newNode;
            newNode.prev = prev;
            newNode.next = curr;
            curr.prev = newNode;
        }

        size++;
        modCount++;
    }

    @Override
    public Byte remove(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();

        Node curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }

        byte val = curr.data;

        if (size == 1) {
            head = null;
        } else {
            curr.prev.next = curr.next;
            curr.next.prev = curr.prev;
            if (curr == head) head = curr.next;
        }

        size--;
        modCount++;
        return val;
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Byte)) return -1;

        Node curr = head;
        for (int i = 0; i < size; i++) {
            if (curr.data == (Byte) o) return i;
            curr = curr.next;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Byte)) return -1;

        int last = -1;
        Node curr = head;
        for (int i = 0; i < size; i++) {
            if (curr.data == (Byte) o) last = i;
            curr = curr.next;
        }
        return last;
    }

        @Override
    public ListIterator<Byte> listIterator() {
        return new NumberListListIterator(0);
    }


    @Override
    public ListIterator<Byte> listIterator(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return new NumberListListIterator(index);
    }

    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex)
            throw new IndexOutOfBoundsException();

        NumberListImpl res = new NumberListImpl();
        for (int i = fromIndex; i < toIndex; i++) {
            res.add(get(i));
        }
        return res;
    }

    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size)
            return false;
        if (index1 == index2) return true;

        Byte tmp = get(index1);
        set(index1, get(index2));
        set(index2, tmp);
        return true;
    }

    @Override
    public void sortAscending() {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (get(j) > get(j + 1)) swap(j, j + 1);
            }
        }
    }

    @Override
    public void sortDescending() {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (get(j) < get(j + 1)) swap(j, j + 1);
            }
        }
    }

    @Override
    public void shiftLeft() {
        if (size > 1) {
            head = head.next;
            modCount++;
        }
    }

    @Override
    public void shiftRight() {
        if (size > 1) {
            head = head.prev;
            modCount++;
        }
    }

    // Ітератор для проходу по списку
    private class NumberListIterator implements Iterator<Byte> {
        private Node nextNode = head;
        private Node lastReturned = null;
        private int index = 0;
        private int expectedModCount = modCount;

        public boolean hasNext() { return index < size; }

        public Byte next() {
            if (modCount != expectedModCount) throw new ConcurrentModificationException();
            if (!hasNext()) throw new NoSuchElementException();
            lastReturned = nextNode;
            nextNode = nextNode.next;
            index++;
            return lastReturned.data;
        }

        public void remove() {
            if (modCount != expectedModCount) throw new ConcurrentModificationException();
            if (lastReturned == null) throw new IllegalStateException();

            // Логіка видалення вузла lastReturned
            if (size == 1) {
                head = null;
            } else {
                lastReturned.prev.next = lastReturned.next;
                lastReturned.next.prev = lastReturned.prev;
                if (lastReturned == head) head = lastReturned.next;
            }
            
            if (lastReturned != nextNode) index--; 
            
            lastReturned = null;
            size--;
            modCount++;
            expectedModCount = modCount;
        }
    }

    // ListIterator з можливістю руху в обидва боки
    private class NumberListListIterator implements ListIterator<Byte> {
        private Node current;
        private Node lastReturned = null;
        private int position;
        private int expectedModCount = modCount;

        NumberListListIterator(int index) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            position = index;
        }

        @Override
        public boolean hasNext() {
            return position < size;
        }

        @Override
        public Byte next() {
            checkForComodification();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            lastReturned = current;
            current = current.next;
            position++;
            return lastReturned.data;
        }

        @Override
        public boolean hasPrevious() {
            return position > 0;
        }

        @Override
        public Byte previous() {
            checkForComodification();
            if (!hasPrevious()) throw new NoSuchElementException();

            current = current.prev; 
            position--;
            lastReturned = current;
            return lastReturned.data;
        }

        @Override
        public int nextIndex() {
            return position;
        }

        @Override
        public int previousIndex() {
            return position - 1;
        }

        @Override
        public void remove() {
            checkForComodification();
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            NumberListImpl.this.remove(position);
            lastReturned = null;
            expectedModCount = modCount;
        }

        @Override
        public void set(Byte e) {
            checkForComodification();
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            NumberListImpl.this.set(position - 1, e);
            expectedModCount = modCount;
        }

        @Override
        public void add(Byte e) {
            checkForComodification();
            NumberListImpl.this.add(position, e);
            position++;
            lastReturned = null;
            expectedModCount = modCount;
        }

        private Node getNodeAt(int index) {
            Node node = head;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
            return node;
        }

        private void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
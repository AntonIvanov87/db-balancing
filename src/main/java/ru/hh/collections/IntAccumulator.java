package ru.hh.collections;

public abstract class IntAccumulator {

  public static IntAccumulator empty() {
    return empty;
  }

  public abstract IntAccumulator add(final int value);

  public abstract int[] toArray();

  private static final class EmptyNode extends IntAccumulator {

    EmptyNode() { }

    @Override
    public ValueNode add(int value) {
      return new ValueNode(value, null);
    }

    @Override
    public int[] toArray() {
      return emptyArr;
    }

    private static final int[] emptyArr = new int[0];
  }

  private static final IntAccumulator empty = new EmptyNode();

  private static final class ValueNode extends IntAccumulator {

    private final int value;
    private final ValueNode prev;

    ValueNode(final int value, final ValueNode prev) {
      this.value = value;
      this.prev = prev;
    }

    @Override
    public ValueNode add(final int value) {
      return new ValueNode(value, this);
    }

    @Override
    public int[] toArray() {

      final int size = size();
      final int[] arr = new int[size];
      ValueNode currentNode = this;
      for (int i=0; i < size; i++) {
        arr[i] = currentNode.value;
        currentNode=currentNode.prev;
      }

      return arr;
    }

    private int size() {

      int currentSize = 1;
      ValueNode currentNode = this;

      while (currentNode.hasPrev()) {
        currentNode = currentNode.prev;
        currentSize++;
      }

      return currentSize;
    }

    private boolean hasPrev() {
      return prev != null;
    }
  }
}

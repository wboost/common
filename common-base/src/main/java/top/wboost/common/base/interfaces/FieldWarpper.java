package top.wboost.common.base.interfaces;

public interface FieldWarpper<K,V> extends Warpper<K,V> {

    public V[] warp(Object[] val);

    public V warpDate(K val);

    public V warpByte(K val);

    public V warpShort(K val);

    public V warpInteger(K val);

    public V warpDouble(K val);

    public V warpFloat(K val);

    public V warpLong(K val);

    public V warpChar(K val);

    public V warpString(K val);

    public V warpObject(K val);

    public V warpArray(K val);

    public V warpCollection(K val);

}

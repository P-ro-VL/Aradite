package com.github.tezvn.aradite.api.packet;

import java.util.List;
import java.util.Map;

public interface PacketPackage<T> {

    /**
     * Return the single form of the data.
     */
   T getSingleData();

    /**
     * Change the single data.
     * @param singleData New Value
     */
   void setSingleData(T singleData);

    /**
     * Return a table of data with keys and values.
     */
   Map<String, T> getMapData();

    /**
     * Return a general list of data.
     */
   List<T> getListData();

    /**
     * Change the single form data.
     *
     * @param singleObject
     *            New single form data.
     */
   void write(T singleObject);

    /**
     * Write a new data to the data map.
     *
     * @param key
     *            Key to indentify
     * @param dataValue
     *            New Value
     */
   void write(String key, T dataValue);

    /**
     * Write a new data to the data list.
     *
     * @param value
     *            New Value
     */
   void add(T value);

    /**
     * Remove a specific data from data list.
     *
     * @param value
     *            Data needs removing.
     */
   void removeFromList(T value);

    /**
     * Remove a specific data whose key is {@code key} from data map
     *
     * @param key
     *            Key of the data that needs removing.
     */
   void removeFromMap(String key);

    /**
     * Return the data in the data map with given {@code key}
     * @param key The data key
     * @return The data
     */
   T retrieveWithKey(String key);

    /**
     * Remove the current single data.
     */
   void clear();

}

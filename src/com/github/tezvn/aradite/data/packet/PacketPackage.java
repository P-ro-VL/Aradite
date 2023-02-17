package com.github.tezvn.aradite.data.packet;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import pdx.mantlecore.java.collection.Lists;

public class PacketPackage<T> {

	private T singleData;
	private Map<String, T> mapData = Maps.newHashMap();
	private List<T> listData = Lists.newArrayList();

	public PacketPackage() {
	}

	/**
	 * Return the single form of the data.
	 */
	public T getSingleData() {
		return singleData;
	}

	/**
	 * Change the single data.
	 * @param singleData New Value
	 */
	public void setSingleData(T singleData) {
		this.singleData = singleData;
	}
	
	/**
	 * Return a table of data with keys and values.
	 */
	public Map<String, T> getMapData() {
		return mapData;
	}

	/**
	 * Return a general list of data.
	 */
	public List<T> getListData() {
		return listData;
	}

	/**
	 * Change the single form data.
	 * 
	 * @param singleObject
	 *            New single form data.
	 */
	public void write(T singleObject) {
		this.singleData = singleObject;
	}

	/**
	 * Write a new data to the data map.
	 * 
	 * @param key
	 *            Key to indentify
	 * @param dataValue
	 *            New Value
	 */
	public void write(String key, T dataValue) {
		this.mapData.put(key, dataValue);
	}

	/**
	 * Write a new data to the data list.
	 * 
	 * @param value
	 *            New Value
	 */
	public void add(T value) {
		this.listData.add(value);
	}

	/**
	 * Remove a specific data from data list.
	 * 
	 * @param value
	 *            Data needs removing.
	 */
	public void removeFromList(T value) {
		this.listData.remove(value);
	}

	/**
	 * Remove a specific data whose key is {@code key} from data map
	 * 
	 * @param key
	 *            Key of the data that needs removing.
	 */
	public void removeFromMap(String key) {
		this.mapData.remove(key);
	}

	/**
	 * Return the data in the data map with given {@code key}
	 * @param key The data key
	 * @return The data
	 */
	public T retrieveWithKey(String key) {
		return this.mapData.get(key);
	}
	
	/**
	 * Remove the current single data.
	 */
	public void clear() {
		this.singleData = null;
	}
}

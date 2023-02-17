package com.github.tezvn.aradite.impl.agent.texture;

import com.github.tezvn.aradite.api.agent.texture.Texture;

public class TextureImpl implements Texture {

	private String data;

	public TextureImpl(String data) {
		this.data = data;
	}

	/**
	 * Return the data of the texture.<br>
	 * If the texture is a file, return its path. However, if texture is for heads,
	 * return URL texture.
	 */
	public String getData() {
		return data;
	}

	/**
	 * Change the texture data.
	 * 
	 * @param data
	 *            New data.
	 */
	public void setData(String data) {
		this.data = data;
	}

}

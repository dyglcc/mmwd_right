/*
 *  Copyright 2010 Yuri Kanivets
import qfpay.wxshop.utils.MobAgentTools;
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package qfpay.wxshop.wheel;

import java.util.ArrayList;

/**
 * The simple Array wheel adapter
 * 
 * @param <T>
 *            the element type
 */
public class ArrayWheelAdapter<T> implements WheelAdapter {

	/** The default items length */
	public static final int DEFAULT_LENGTH = -1;

	// items
	private ArrayList<String> items;
	// length
//	private int length;

	/**
	 * Constructor
	 * 
	 * @param items
	 *            the items
	 * @param length
	 *            the max items length
	 */
	public ArrayWheelAdapter(ArrayList<String> list, int length) {
		this.items = list;
//		this.length = length;
	}

	// /**
	// * Contructor
	// * @param items the items
	// */
	// public ArrayWheelAdapter(T items[]) {
	// this(items, DEFAULT_LENGTH);
	// }

	@Override
	public String getItem(int index) {
		if (index >= 0 && index < items.size()) {
			return items.get(index);
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		return items.size();
	}

	@Override
	public int getMaximumLength() {
		return items == null ? 0 :items.size();
	}
	
	public void setData(ArrayList<String> data){
		items = data;
	}

}

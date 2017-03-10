/*
 *  Copyright 2010 Yuri Kanivets
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

package com.cjx.zhiai.component.wheel;

/**
 * Numeric Wheel adapter.
 */
public class RangeWheelAdapter implements WheelAdapter {

	// Values
	private int minValue;
	private int maxValue;
	private String auto, unit;

	/**
	 * Constructor
	 * 
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 * @param format
	 *            the format string
	 */
	public RangeWheelAdapter(int minValue, int maxValue, String auto,
			String unit) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.auto = auto;
		this.unit = unit;
	}

	@Override
	public String getItem(int index) {
		if (index == 0) {
			return auto;
		} else {
			return Integer.toString(minValue + index - 1) + unit;
		}
	}

	@Override
	public int getItemsCount() {
		return maxValue - minValue + 2;
	}

	@Override
	public int getMaximumLength() {
		int max = Math.max(Math.abs(maxValue), Math.abs(minValue));
		int maxLen = Integer.toString(max).length();
		if (minValue < 0) {
			maxLen++;
		}
		return maxLen;
	}
}

package com.cjx.zhiai.component.wheel;

import java.util.ArrayList;

public class StringWheelAdapter implements WheelAdapter {

    ArrayList<String> list;

    public StringWheelAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @Override
    public int getItemsCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public String getItem(int index) {
        return list.get(index);
    }

    @Override
    public int getMaximumLength() {
        int length = 0;
        for (String s : list) {
            int l = s.length();
            length = Math.max(length, l);
        }
        return length;
    }

}

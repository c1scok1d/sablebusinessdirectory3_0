package com.macinternetservices.sablebusinessdirectory.utils;

/**
 * Created by MAC Internet Services on 7/25/15.
 * Contact Email : admin@thesablebusinessdirectory.com
 */


public interface SelectListener {
    void Select(int position, CharSequence text);
    void Select(int position, CharSequence text, String id);
    /*void Select(View view, int position, CharSequence text, String id, float additional_price);*/
}

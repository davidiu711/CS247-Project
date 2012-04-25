package com.csis247.theApp;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/** this class implements an itemized overlay for the map. I don't think anything
 * here needs to change.
 */
public class MapItemizedOverlay extends ItemizedOverlay<OverlayItem> {
    
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    public MapItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
        // TODO Auto-generated constructor stub
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }
    
    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }

}
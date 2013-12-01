package com.example.stickablelistview;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ListAdapter;

public class StickListViewV2 extends ViewGroup {
	
	private GestureDetector gestureDectector;
	
	private ListAdapter adapter;
	
	private Queue<View> recycleBox;
	
	private int height;
	private int childHeight;
	
	private boolean isDataChanged;
	
	private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
		
		@Override
		public boolean onDown(MotionEvent event){
			//TODO StickListViewV2 onDown method
			return true;
		}
		
		@Override
		public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY){
			//TODO StickListViewV2 onScroll method
			return true;
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent event){
			//TODO StickListViewV2 onSingleTapConfirmed method
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent event){
			//TODO StickListViewV2 onLongPress method
		}
	};
	
	private DataSetObserver stickListDataSetObserver = new DataSetObserver(){
		
		@Override
		public void onChanged(){
			isDataChanged = true;
			requestLayout();
			invalidate();
		}
		
		@Override
		public void onInvalidated(){
			requestLayout();
			invalidate();
		}
	};

	public StickListViewV2(Context context){
		this(context, null);
	}
	
	public StickListViewV2(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public StickListViewV2(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		init();
	}
	
	private void init(){
		recycleBox = new LinkedList<View>();
	}

	public void setAdapter(ListAdapter adapter){
		if(this.adapter != null){
			this.adapter.unregisterDataSetObserver(stickListDataSetObserver);
		}
		
		if(adapter != null){
			this.adapter = adapter;
			
			this.adapter.registerDataSetObserver(stickListDataSetObserver);
		}
		
		
		isDataChanged = true;
		
		removeAllViewsInLayout();
		requestLayout();
	}
	
	public ListAdapter getAdapter(){
		return adapter;
	}
	
	private View obtainViewForLayout(int position){
		if(adapter == null){
			throw new NullPointerException();
		}
		
		View result = recycleBox.poll();
		result = adapter.getView(position, result, this);
		
		return result;
	}
	
	private void initRecycleBox(){
		if(recycleBox != null){
			recycleBox.clear();
		}else{
			recycleBox = new LinkedList<View>();
		}
	}
	
	private boolean recycleView(View target){
		return recycleBox.offer(target);
	}
	
	private int getMaxViewOnScreen(){
		if(adapter == null){
			return -1;
		}
		
		int maxAvaiableCount = (int) Math.ceil(height / childHeight);
		
		return adapter.getCount() > maxAvaiableCount ? maxAvaiableCount + 1 : adapter.getCount();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//TODO StickListViewV2 measure
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//TODO StickListViewV2 measure & layout children
	}
}

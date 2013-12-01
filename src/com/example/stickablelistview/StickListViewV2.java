package com.example.stickablelistview;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ListAdapter;

public class StickListViewV2 extends ViewGroup {
	
	private GestureDetector gestureDetector;
	
	private ListAdapter adapter;
	
	private Queue<View> recycleBox;
	
	private int width;
	private int height;
	private int childHeight;
	
	private boolean isDataChanged;
	
	private int adapterViewIndex;
	
	private int currentScrollY;
	private int deltaY;
	private int currentPositionOnScreen;
	
	private int widthMeasureSpec;
	private int heightMeasureSpec;
	
	private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
		
		@Override
		public boolean onDown(MotionEvent event){
			//TODO StickListViewV2 onDown method
			return true;
		}
		
		@Override
		public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY){
			//TODO StickListViewV2 onScroll method
			currentScrollY -= distanceY;
			requestLayout();
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
	
	private View.OnTouchListener gestureHandler = new View.OnTouchListener(){

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return gestureDetector.onTouchEvent(event);
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
		
		gestureDetector = new GestureDetector(context, gestureListener);
		setOnTouchListener(gestureHandler);
		init();
	}
	
	private void init(){
		recycleBox = new LinkedList<View>();
		adapterViewIndex = 0;
		currentScrollY = 0;
		deltaY = 0;
		currentPositionOnScreen = 0;
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
	
	private void calculateDeltaY(){
		int i = 0;
		deltaY = currentScrollY;
		if(currentScrollY < 0){
			int count = (int) Math.floor((-currentScrollY) / childHeight);
			deltaY += childHeight * count;
			currentPositionOnScreen = count;
		}else if(currentScrollY > height){
			currentPositionOnScreen = -1;
		}
		
		
	}
	
	private void fillLayoutWithViews(){
		while(adapterViewIndex < adapter.getCount() && deltaY + childHeight * adapterViewIndex < height){
			
			addAndMeasureChild(obtainViewForLayout(adapterViewIndex), adapterViewIndex);
			adapterViewIndex++;
		}
		
		Log.d("this", getChildCount() + "");
	}
	
	private void addAndMeasureChild(View child, int position){
		addViewInLayout(child, position, getLayoutParams(child), true);
		measureChild(child);
		
		if(childHeight == 0) childHeight = child.getMeasuredHeight();
	}
	
	private void measureChild(View child){
		ViewGroup.LayoutParams childLayoutParams = getLayoutParams(child);
		int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), childLayoutParams.width);

        int childHeightSpec;
        if (childLayoutParams.height > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(childLayoutParams.height, MeasureSpec.EXACTLY);
        } else {
        	childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        child.measure(childWidthSpec, childHeightSpec);
	}
	
	private ViewGroup.LayoutParams getLayoutParams(View child){
		ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        return layoutParams;
	}
	
	private void layoutChildren(){
		calculateDeltaY();
		final int count = getChildCount();
		View child;
		for(int i = 0; i < count; i++){
			child = getChildAt(i);
			int top = deltaY + childHeight * i;
			int left = getLeft() + getPaddingLeft();
			int right = getRight() + getPaddingRight();
			int bottom = top + childHeight;
			
			Log.d("this", left + ":" + top + ":" + right + ":" + bottom);
			
			child.layout(left, top, right, bottom);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		this.widthMeasureSpec = widthMeasureSpec;
		this.heightMeasureSpec = heightMeasureSpec;
		
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		if(isDataChanged){
			removeAllViewsInLayout();
			fillLayoutWithViews();
			isDataChanged = false;
		}
		
		layoutChildren();
	}
}

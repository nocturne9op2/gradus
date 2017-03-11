package com.gda.data.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter;
import com.gda.data.R;
import com.gda.data.entities.Course;
import com.gda.data.utilities.DecoViewUtils;
import com.gda.data.utilities.DimensionsUtils;
import com.gda.data.utilities.MathUtils;
import com.hookedonplay.decoviewlib.DecoView;

import java.util.ArrayList;
import java.util.Locale;

public class MainAdapter extends DragSelectRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private ItemClickListener callback;
    private ArrayList<Course> courses;

    private double gpa;
    private double units;

    private ArrayList<Course> flatItems;

    public MainAdapter(ItemClickListener callback, ArrayList<Course> courses) {
        super();

        this.callback = callback;
        this.courses = courses;

        updateGPAandUnits();

        generateFlatItems(courses);
    }

    private void generateFlatItems(ArrayList<Course> courses) {
        flatItems = new ArrayList<>();
        flatItems.add(null);
        for (int i = 0; i < courses.size(); i++) {
            flatItems.add(courses.get(i));
        }
    }

    public interface ItemClickListener {
        void onItemClick(int i);

        void onItemLongClick(int i);
    }

    private class ViewHolderHeader extends RecyclerView.ViewHolder {
        TextView gpa;
        TextView units;

        ViewHolderHeader(View view) {
            super(view);
            gpa = (TextView) view.findViewById(R.id.textViewGpa);
            units = (TextView) view.findViewById(R.id.textViewUnits);
        }
    }

    private class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ItemClickListener callback;
        DecoView decoView;
        TextView courseName;
        TextView courseLetter;

        ViewHolderItem(View view, ItemClickListener callback) {
            super(view);
            this.callback = callback;
            decoView = (DecoView) view.findViewById(R.id.dynamicArcView);
            courseName = (TextView) view.findViewById(R.id.textViewCourseName);
            courseLetter = (TextView) view.findViewById(R.id.textViewCourseLetter);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (callback != null) {
                callback.onItemClick(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (callback != null) {
                callback.onItemLongClick(getAdapterPosition());
            }
            return true;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_header, parent, false);
            return new ViewHolderHeader(view);
        } else if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
            return new ViewHolderItem(view, callback);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (holder instanceof ViewHolderHeader) {
            ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
            onBindViewHolderHeader(viewHolderHeader);
        } else if (holder instanceof ViewHolderItem) {
            ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            onBindViewHolderItem(viewHolderItem, flatItems.get(position), position);
        }
    }

    private void onBindViewHolderHeader(ViewHolderHeader viewHolderHeader) {
        viewHolderHeader.gpa.setText("GPA: " + String.format(Locale.getDefault(), "%.2f", gpa));
        viewHolderHeader.units.setText("Units: " + String.format(Locale.getDefault(), "%.2f", units));
    }

    private void onBindViewHolderItem(ViewHolderItem viewHolderItem, Course course, int position) {
        TextView courseLetter = viewHolderItem.courseLetter;
        courseLetter.setText(course.getLetter());
        courseLetter.setAlpha(1);

        TextView courseName = viewHolderItem.courseName;
        courseName.setText(course.getName() + " / " + String.format(Locale.getDefault(), "%.2f", course.getUnits()));
        courseName.setAlpha(1);

        DecoView decoView = viewHolderItem.decoView;
        decoView.deleteAll();

        decoView.addSeries(DecoViewUtils.buildBase(true, DimensionsUtils.smallDecoViewLineWidth));
        decoView.addSeries(DecoViewUtils.buildSeries(course.getColor(), (float) course.getPercent(), DimensionsUtils.smallDecoViewLineWidth, null));

        if (isIndexSelected(position)) {
            courseLetter.setAlpha(37 / 255f);
            courseName.setAlpha(37 / 255f);

            decoView.addSeries(DecoViewUtils.buildSeries(Color.argb(218, 47, 79, 79), 100, DimensionsUtils.smallDecoViewLineWidth, null));
        }
    }

    @Override
    public int getItemCount() {
        return flatItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    protected boolean isIndexSelectable(int index) {
        return index != 0;
    }

    public void addCourse(Course item) {
        courses.add(0, item);

        updateGPAandUnits();
        generateFlatItems(courses);

        notifyItemChanged(0);
        notifyItemInserted(1);
    }

    public void removeCourse(int position) {
        courses.remove(position);

        updateGPAandUnits();
        generateFlatItems(courses);

        notifyItemChanged(0);
        notifyItemRemoved(position + 1);
    }

    private void updateGPAandUnits() {
        gpa = MathUtils.GPA(courses);
        units = MathUtils.units(courses);
    }

    public Course get(int position) {
        return flatItems.get(position);
    }
}
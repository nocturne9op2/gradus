package com.gda.data.adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter;
import com.gda.data.R;
import com.gda.data.entities.Assessment;
import com.gda.data.entities.AssessmentType;
import com.gda.data.entities.Course;
import com.gda.data.entities.SectionWrapper;
import com.gda.data.utilities.ColorUtils;
import com.gda.data.utilities.DecoViewUtils;
import com.gda.data.utilities.DimensionsUtils;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.charts.SeriesLabel;

import java.util.ArrayList;
import java.util.Locale;

public class CourseAdapter extends DragSelectRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_SECTION = 1;
    private static final int TYPE_CHILD = 2;

    private ItemClickListener callback;
    private Course course;
    private ArrayList<AssessmentType> assessmentTypes;

    private boolean isInitialSetup;

    private ArrayList<SectionWrapper> flatItems;

    public CourseAdapter(ItemClickListener callback, Course course, ArrayList<AssessmentType> assessmentTypes) {
        super();

        this.callback = callback;
        this.course = course;
        this.assessmentTypes = assessmentTypes;

        isInitialSetup = true;

        generateFlatItems(assessmentTypes);
    }

    private void generateFlatItems(ArrayList<AssessmentType> assessmentTypes) {
        flatItems = new ArrayList<>();
        flatItems.add(null);
        for (int i = 0; i < assessmentTypes.size(); i++) {
            generateSectionWrapper(assessmentTypes.get(i), i);
        }
    }

    private void generateSectionWrapper(AssessmentType assessmentType, int sectionPosition) {
        flatItems.add(new SectionWrapper(assessmentType, sectionPosition));
        ArrayList<Assessment> assessments = assessmentType.getAssessments();
        for (int i = 0; i < assessments.size(); i++) {
            flatItems.add(new SectionWrapper(assessments.get(i), sectionPosition, i));
        }
    }

    public interface ItemClickListener {
        void onItemClick(int i);

        void onItemLongClick(int i);
    }

    private class ViewHolderHeader extends RecyclerView.ViewHolder {
        DecoView decoView;
        TextView coursePercent;

        ViewHolderHeader(View view) {
            super(view);
            decoView = (DecoView) view.findViewById(R.id.dynamicArcView);
            coursePercent = (TextView) view.findViewById(R.id.textViewCoursePercent);
        }
    }

    private class ViewHolderSection extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ItemClickListener callback;
        TextView assessmentTypeName;
        TextView assessmentTypeGradePortion;
        View colorOverlay;

        ViewHolderSection(View view, ItemClickListener callback) {
            super(view);
            this.callback = callback;
            assessmentTypeName = (TextView) view.findViewById(R.id.textViewAssessmentTypeName);
            assessmentTypeGradePortion = (TextView) view.findViewById(R.id.textViewAssessmentTypeGradePortion);
            colorOverlay = view.findViewById(R.id.colorOverlay);
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

    private class ViewHolderChild extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ItemClickListener callback;
        TextView assessmentName;
        TextView assessmentScoreAndTotal;
        View colorOverlay;

        ViewHolderChild(View view, ItemClickListener callback) {
            super(view);
            this.callback = callback;
            assessmentName = (TextView) view.findViewById(R.id.textViewAssessmentName);
            assessmentScoreAndTotal = (TextView) view.findViewById(R.id.textViewAssessmentScoreAndTotal);
            colorOverlay = view.findViewById(R.id.colorOverlay);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header, parent, false);
            return new ViewHolderHeader(view);
        } else if (viewType == TYPE_SECTION) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_section, parent, false);
            return new ViewHolderSection(view, callback);
        } else if (viewType == TYPE_CHILD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_child, parent, false);
            return new ViewHolderChild(view, callback);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (holder instanceof ViewHolderHeader) {
            ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
            onBindViewHolderHeader(viewHolderHeader);
        } else {
            SectionWrapper sectionWrapper = flatItems.get(position);

            if (holder instanceof ViewHolderSection) {
                ViewHolderSection viewHolderSection = (ViewHolderSection) holder;
                onBindViewHolderSection(viewHolderSection, sectionWrapper.getSection(), position);
            } else if (holder instanceof ViewHolderChild) {
                ViewHolderChild viewHolderChild = (ViewHolderChild) holder;
                onBindViewHolderChild(viewHolderChild, sectionWrapper.getChild(), position);
            }
        }
    }

    private void onBindViewHolderHeader(ViewHolderHeader viewHolderHeader) {
        final TextView coursePercent = viewHolderHeader.coursePercent;
        coursePercent.setText("0");

        DecoView decoView = viewHolderHeader.decoView;
        decoView.deleteAll();

        if (isInitialSetup) {
            decoView.addSeries(DecoViewUtils.buildBase(false, DimensionsUtils.largeDecoViewLineWidth));
            decoView.addEvent(DecoViewUtils.buildBaseShowEvent(Resources.getSystem().getInteger(android.R.integer.config_shortAnimTime)));

            isInitialSetup = false;
        } else {
            decoView.addSeries(DecoViewUtils.buildBase(true, DimensionsUtils.largeDecoViewLineWidth));
        }

        if (course.isWeighted() && !assessmentTypes.isEmpty()) {
            int color = course.getColor();
            double assessmentTypePercentPool = course.getPercent();

            for (int i = 0; i < assessmentTypes.size(); i++) {
                SeriesItem assessmentTypeSeriesItem = DecoViewUtils.buildSeries(color, 0, DimensionsUtils.largeDecoViewLineWidth, new SeriesLabel.Builder(assessmentTypes.get(i).getName()).build());

                if (i == 0) {
                    assessmentTypeSeriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
                        @Override
                        public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                            coursePercent.setText(String.format(Locale.getDefault(), "%.0f", currentPosition));
                        }

                        @Override
                        public void onSeriesItemDisplayProgress(float percentComplete) {
                        }
                    });
                }

                int assessmentTypeSeriesIndex = decoView.addSeries(assessmentTypeSeriesItem);

                decoView.addEvent(DecoViewUtils.buildSeriesShowEvent((float) assessmentTypePercentPool, assessmentTypeSeriesIndex,
                        Resources.getSystem().getInteger(android.R.integer.config_shortAnimTime), Resources.getSystem().getInteger(android.R.integer.config_longAnimTime)));

                color = ColorUtils.changeColor(color, 0.9f);
                assessmentTypePercentPool -= assessmentTypes.get(i).getPercent();
            }
        } else {
            SeriesItem seriesItem = DecoViewUtils.buildSeries(course.getColor(), 0, DimensionsUtils.largeDecoViewLineWidth, null);

            seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
                @Override
                public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                    coursePercent.setText(String.format(Locale.getDefault(), "%.0f", currentPosition));
                }

                @Override
                public void onSeriesItemDisplayProgress(float percentComplete) {
                }
            });

            int seriesIndex = decoView.addSeries(seriesItem);

            decoView.addEvent(DecoViewUtils.buildSeriesShowEvent((float) course.getPercent(), seriesIndex,
                    Resources.getSystem().getInteger(android.R.integer.config_shortAnimTime), Resources.getSystem().getInteger(android.R.integer.config_longAnimTime)));
        }
    }

    private void onBindViewHolderSection(ViewHolderSection viewHolderSection, AssessmentType assessmentType, int position) {
        viewHolderSection.assessmentTypeName.setText(assessmentType.getName());
        if (course.isWeighted()) {
            viewHolderSection.assessmentTypeGradePortion.setText(assessmentType.getGradePortion() + "%");
        }

        View colorOverlay = viewHolderSection.colorOverlay;
        colorOverlay.setAlpha(0);

        if (isIndexSelected(position)) {
            colorOverlay.setAlpha(1);
        }
    }

    private void onBindViewHolderChild(ViewHolderChild viewHolderChild, Assessment assessment, int position) {
        viewHolderChild.assessmentName.setText(assessment.getName());
        viewHolderChild.assessmentScoreAndTotal.setText(String.format(Locale.getDefault(), "%.2f", assessment.getScore()) + " / " + assessment.getTotal());

        View colorOverlay = viewHolderChild.colorOverlay;
        colorOverlay.setAlpha(0);

        if (isIndexSelected(position)) {
            colorOverlay.setAlpha(1);
        }
    }

    @Override
    public int getItemCount() {
        return flatItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return TYPE_HEADER;
        } else if (isSection(position)) {
            return TYPE_SECTION;
        } else {
            return TYPE_CHILD;
        }
    }

    private boolean isHeader(int position) {
        return position == 0;
    }

    private boolean isSection(int position) {
        return flatItems.get(position).isSection();
    }

    @Override
    protected boolean isIndexSelectable(int index) {
        return index != 0;
    }

    public void addAssessmentTypeAndAssessment(AssessmentType assessmentType, Assessment assessment) {
        assessmentTypes.add(0, assessmentType);
        assessmentTypes.get(0).getAssessments().add(0, assessment);

        updateCoursePercentage(course.isWeighted());
        generateFlatItems(assessmentTypes);

        notifyItemRangeChanged(0, getItemCount());
    }

    public void addAssessment(Assessment assessment, int sectionPosition) {
        assessmentTypes.get(sectionPosition).getAssessments().add(0, assessment);

        updateCoursePercentage(course.isWeighted());
        generateFlatItems(assessmentTypes);

        notifyItemRangeChanged(0, getItemCount());
    }

    public void removeAssessmentType(int position) {
        assessmentTypes.remove(position);

        updateCoursePercentage(course.isWeighted());
        generateFlatItems(assessmentTypes);

        notifyItemRangeChanged(0, getItemCount());
    }

    public void removeAssessment(int sectionPosition, int position) {
        assessmentTypes.get(sectionPosition).getAssessments().remove(position);

        updateCoursePercentage(course.isWeighted());
        generateFlatItems(assessmentTypes);

        notifyItemRangeChanged(0, getItemCount());
    }

    private void updateCoursePercentage(boolean weighted) {
        course.updatePercent(weighted);
    }

    public SectionWrapper get(int position) {
        return flatItems.get(position);
    }
}
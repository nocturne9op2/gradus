package com.gda.data.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.dragselectrecyclerview.DragSelectRecyclerView;
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter;
import com.gda.data.R;
import com.gda.data.activities.MainActivity;
import com.gda.data.adapters.CourseAdapter;
import com.gda.data.entities.Assessment;
import com.gda.data.entities.AssessmentType;
import com.gda.data.entities.Course;
import com.gda.data.entities.SectionWrapper;
import com.gda.data.layoutmanagers.CustomLinearLayoutManager;
import com.gda.data.utilities.AppStateUtils;
import com.gda.data.utilities.DialogUtils;
import com.gda.data.utilities.ToolbarUtils;

import java.util.ArrayList;

public class CourseFragment extends BaseFragment implements CourseAdapter.ItemClickListener, DragSelectRecyclerViewAdapter.SelectionListener {
    private MainActivity activity;

    private TextView toolbarTitle;
    private ToolbarUtils.ShowHideToolbarOnScrollingListener showHideToolbarListener;

    private Menu menu;

    private Course course;
    private ArrayList<AssessmentType> assessmentTypes;

    private CourseAdapter courseAdapter;

    private CustomLinearLayoutManager layoutManager;

    private DragSelectRecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        activity = (MainActivity) getActivity();

        toolbarTitle = activity.getToolbarTitle();
        showHideToolbarListener = activity.getShowHideToolbarListener();

        course = getArguments().getParcelable("course");
        assessmentTypes = course.getAssessmentTypes();

        AppStateUtils.setItemDeletionState(false);

        toolbarTitle.setText(course.getName());
        showHideToolbarListener.showToolbar();

        setHasOptionsMenu(true);

        courseAdapter = new CourseAdapter(this, course, assessmentTypes);
        courseAdapter.setSelectionListener(this);

        layoutManager = new CustomLinearLayoutManager(activity);

        recyclerView = (DragSelectRecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.getItemAnimator().setChangeDuration(recyclerView.getItemAnimator().getAddDuration());
        recyclerView.setOnScrollListener(showHideToolbarListener);
        recyclerView.setAdapter(courseAdapter);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    //toolbar methods

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;

        if (course.isWeighted()) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_weight_grey600_24dp);
        } else {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        menu.add(Menu.NONE, R.id.action_delete_item, Menu.NONE, "Delete Assessment")
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                .setIcon(R.mipmap.ic_delete_white_24dp)
                .setVisible(false);

        menu.add(Menu.NONE, R.id.action_add_item, Menu.NONE, "Add Assessment")
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                .setIcon(R.mipmap.ic_add_custom_24dp);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_item:
                View view = (LayoutInflater.from(activity).inflate(R.layout.dialog_assessment_input, null));

                final EditText assessmentNameInput = (EditText) view.findViewById(R.id.editTextAssessmentNameInput);
                final EditText assessmentScoreInput = (EditText) view.findViewById(R.id.editTextAssessmentScoreInput);
                final EditText assessmentTotalInput = (EditText) view.findViewById(R.id.editTextAssessmentTotalInput);
                final AutoCompleteTextView assessmentTypeSelection = (AutoCompleteTextView) view.findViewById(R.id.autoTextViewAssessmentTypeSelection);
                final EditText assessmentTypeGradePortionInput = (EditText) view.findViewById(R.id.editTextAssessmentGradePortionInput);
                final TextView percentSign = (TextView) view.findViewById(R.id.textViewPercentSign);

                assessmentTypeSelection.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, assessmentTypes));

                if (course.isWeighted()) {
                    assessmentTypeSelection.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String assessmentTypeName = assessmentTypeSelection.getText().toString().trim();
                            if (course.containsAssessmentType(assessmentTypeName)) {
                                assessmentTypeGradePortionInput.setText(String.valueOf(course.getAssessmentTypeFromName(assessmentTypeName).getGradePortion()));
                                assessmentTypeGradePortionInput.setEnabled(false);
                                percentSign.setEnabled(false);
                            } else {
                                assessmentTypeGradePortionInput.setEnabled(true);
                                percentSign.setEnabled(true);
                            }
                        }
                    });
                } else {
                    assessmentTypeGradePortionInput.setVisibility(View.INVISIBLE);
                    percentSign.setVisibility(View.INVISIBLE);
                }

                DialogUtils.showInputDialog(activity, view, new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialogInterface) {
                        Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String assessmentName = assessmentNameInput.getText().toString().trim();
                                String assessmentScoreString = assessmentScoreInput.getText().toString();
                                double assessmentScore = assessmentScoreString.equals("") ? 0 : Double.valueOf(assessmentScoreString);
                                String assessmentTotalString = assessmentTotalInput.getText().toString();
                                int assessmentTotal = assessmentTotalString.equals("") ? 0 : Integer.valueOf(assessmentTotalString);

                                String assessmentTypeName = assessmentTypeSelection.getText().toString().trim();
                                String assessmentTypeGradePortionString = assessmentTypeGradePortionInput.getText().toString();
                                int assessmentTypeGradePortion = assessmentTypeGradePortionString.equals("") ? 0 : Integer.valueOf(assessmentTypeGradePortionString);

                                if ((course.isWeighted() && assessmentName.length() > 0 && assessmentTotal > 0 && assessmentTypeName.length() > 0 && assessmentTypeGradePortion > 0)
                                        || (!course.isWeighted() && assessmentName.length() > 0 && assessmentTotal > 0 && assessmentTypeName.length() > 0)) {
                                    layoutManager.scrollToPosition(0);

                                    if (course.containsAssessmentType(assessmentTypeName)) {
                                        courseAdapter.addAssessment(new Assessment(assessmentName, assessmentScore, assessmentTotal), course.getAssessmentTypeIndexFromName(assessmentTypeName));
                                    } else {
                                        courseAdapter.addAssessmentTypeAndAssessment(new AssessmentType(assessmentTypeName, assessmentTypeGradePortion), new Assessment(assessmentName, assessmentScore, assessmentTotal));
                                    }
                                    dialogInterface.dismiss();
                                }
                            }
                        });
                    }
                });

                return true;
            case R.id.action_delete_item:
                layoutManager.scrollToPosition(0);

                for (int i = courseAdapter.getItemCount() - 1; i >= 0; i--) {
                    if (courseAdapter.isIndexSelected(i)) {
                        courseAdapter.toggleSelected(i);

                        SectionWrapper sectionWrapper = courseAdapter.get(i);
                        if (sectionWrapper.isSection()) {
                            courseAdapter.removeAssessmentType(sectionWrapper.getSectionPosition());
                        } else {
                            courseAdapter.removeAssessment(sectionWrapper.getSectionPosition(), sectionWrapper.getChildPosition());
                        }
                    }
                }

                AppStateUtils.setItemDeletionState(false);
                ToolbarUtils.updateItemDeletionStateIcons(activity, menu, course, AppStateUtils.isItemDeletionState);

                return true;
            case android.R.id.home:
                if (AppStateUtils.isItemDeletionState) {
                    for (int i = courseAdapter.getItemCount() - 1; i >= 0; i--) {
                        if (courseAdapter.isIndexSelected(i)) {
                            courseAdapter.toggleSelected(i);
                        }
                    }

                    AppStateUtils.setItemDeletionState(false);
                    ToolbarUtils.updateItemDeletionStateIcons(activity, menu, course, AppStateUtils.isItemDeletionState);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //adapter methods

    @Override
    public void onItemClick(int index) {
        if (AppStateUtils.isItemDeletionState) {
            courseAdapter.toggleSelected(index);
        }
    }

    @Override
    public void onItemLongClick(int index) {
        if (!AppStateUtils.isItemDeletionState) {
            AppStateUtils.setItemDeletionState(true);
            ToolbarUtils.updateItemDeletionStateIcons(activity, menu, course, AppStateUtils.isItemDeletionState);

            showHideToolbarListener.animateShowToolbar();
        }

        recyclerView.setDragSelectActive(true, index);
    }

    @Override
    public void onDragSelectionChanged(int count) {
        if (count == 0) {
            AppStateUtils.setItemDeletionState(false);
            ToolbarUtils.updateItemDeletionStateIcons(activity, menu, course, AppStateUtils.isItemDeletionState);
        }
    }

    @Override
    public void onBackPressed() {
        for (int i = courseAdapter.getItemCount() - 1; i >= 0; i--) {
            if (courseAdapter.isIndexSelected(i)) {
                courseAdapter.toggleSelected(i);
            }
        }

        AppStateUtils.setItemDeletionState(false);
        ToolbarUtils.updateItemDeletionStateIcons(activity, menu, course, AppStateUtils.isItemDeletionState);
    }
}
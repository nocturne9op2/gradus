package com.gda.data.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.dragselectrecyclerview.DragSelectRecyclerView;
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter;
import com.gda.data.R;
import com.gda.data.activities.MainActivity;
import com.gda.data.adapters.MainAdapter;
import com.gda.data.entities.Course;
import com.gda.data.utilities.AppStateUtils;
import com.gda.data.utilities.DialogUtils;
import com.gda.data.utilities.ToolbarUtils;

import java.util.ArrayList;

public class MainFragment extends BaseFragment implements MainAdapter.ItemClickListener, DragSelectRecyclerViewAdapter.SelectionListener {
    private MainActivity activity;

    private TextView toolbarTitle;
    private ToolbarUtils.ShowHideToolbarOnScrollingListener showHideToolbarListener;

    private Menu menu;

    private ArrayList<Course> courses;

    private MainAdapter mainAdapter;

    private GridLayoutManager layoutManager;

    private DragSelectRecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        activity = (MainActivity) getActivity();

        toolbarTitle = activity.getToolbarTitle();
        showHideToolbarListener = activity.getShowHideToolbarListener();

        courses = getArguments().getParcelableArrayList("courses");

        AppStateUtils.setItemDeletionState(false);

        toolbarTitle.setText("");
        showHideToolbarListener.showToolbar();

        setHasOptionsMenu(true);

        mainAdapter = new MainAdapter(this, courses);
        mainAdapter.setSelectionListener(this);

        layoutManager = new GridLayoutManager(activity, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mainAdapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });

        recyclerView = (DragSelectRecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setOnScrollListener(showHideToolbarListener);
        recyclerView.setAdapter(mainAdapter);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    //toolbar methods

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        menu.add(Menu.NONE, R.id.action_delete_item, Menu.NONE, "Delete Course")
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                .setIcon(R.mipmap.ic_delete_white_24dp)
                .setVisible(false);

        menu.add(Menu.NONE, R.id.action_add_item, Menu.NONE, "Add Course")
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                .setIcon(R.mipmap.ic_add_custom_24dp);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_item:
                View view = (LayoutInflater.from(activity).inflate(R.layout.dialog_course_input, null));

                final EditText courseNameInput = (EditText) view.findViewById(R.id.editTextCourseNameInput);
                final EditText courseUnitsInput = (EditText) view.findViewById(R.id.editTextCourseUnitsInput);
                final CheckBox courseWeighted = (CheckBox) view.findViewById(R.id.checkBoxCourseWeighted);

                DialogUtils.showInputDialog(activity, view, new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialogInterface) {
                        Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String courseName = courseNameInput.getText().toString().trim();
                                String courseUnitsString = courseUnitsInput.getText().toString();
                                double courseUnits = courseUnitsString.equals("") ? 0 : Double.valueOf(courseUnitsString);

                                if (courseName.length() > 0 && courseUnits > 0) {
                                    layoutManager.scrollToPosition(0);

                                    mainAdapter.addCourse(new Course(courseName, courseUnits, courseWeighted.isChecked()));
                                    dialogInterface.dismiss();
                                }
                            }
                        });
                    }
                });

                return true;
            case R.id.action_delete_item:
                layoutManager.scrollToPosition(0);

                for (int i = mainAdapter.getItemCount() - 1; i >= 0; i--) {
                    if (mainAdapter.isIndexSelected(i)) {
                        mainAdapter.toggleSelected(i);
                        mainAdapter.removeCourse(i - 1);
                    }
                }

                AppStateUtils.setItemDeletionState(false);
                ToolbarUtils.updateItemDeletionStateIcons(activity, menu, null, AppStateUtils.isItemDeletionState);

                return true;
            case android.R.id.home:
                if (AppStateUtils.isItemDeletionState) {
                    for (int i = mainAdapter.getItemCount() - 1; i >= 0; i--) {
                        if (mainAdapter.isIndexSelected(i)) {
                            mainAdapter.toggleSelected(i);
                        }
                    }

                    AppStateUtils.setItemDeletionState(false);
                    ToolbarUtils.updateItemDeletionStateIcons(activity, menu, null, AppStateUtils.isItemDeletionState);
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
            mainAdapter.toggleSelected(index);
        } else {
            Bundle arguments = new Bundle();
            arguments.putParcelable("course", mainAdapter.get(index));

            CourseFragment courseFragment = new CourseFragment();
            courseFragment.setArguments(arguments);

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(0, 0, R.animator.fade_in, 0)
                    .replace(R.id.fragmentContainer, courseFragment, "CourseFragment")
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onItemLongClick(int index) {
        if (!AppStateUtils.isItemDeletionState) {
            AppStateUtils.setItemDeletionState(true);
            ToolbarUtils.updateItemDeletionStateIcons(activity, menu, null, AppStateUtils.isItemDeletionState);

            showHideToolbarListener.animateShowToolbar();
        }

        recyclerView.setDragSelectActive(true, index);
    }

    @Override
    public void onDragSelectionChanged(int count) {
        if (count == 0) {
            AppStateUtils.setItemDeletionState(false);
            ToolbarUtils.updateItemDeletionStateIcons(activity, menu, null, AppStateUtils.isItemDeletionState);
        }
    }

    @Override
    public void onBackPressed() {
        for (int i = mainAdapter.getItemCount() - 1; i >= 0; i--) {
            if (mainAdapter.isIndexSelected(i)) {
                mainAdapter.toggleSelected(i);
            }
        }

        AppStateUtils.setItemDeletionState(false);
        ToolbarUtils.updateItemDeletionStateIcons(activity, menu, null, AppStateUtils.isItemDeletionState);
    }
}
package com.gda.data.utilities;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.gda.data.R;
import com.gda.data.activities.MainActivity;
import com.gda.data.entities.Course;

public final class ToolbarUtils {
    public static class ShowHideToolbarOnScrollingListener extends RecyclerView.OnScrollListener {
        private Toolbar toolbar;

        private int verticalOffset;
        private int scrollingOffset;

        public ShowHideToolbarOnScrollingListener(Toolbar toolbar) {
            this.toolbar = toolbar;
        }

        public final void showToolbar() {
            toolbar.animate().cancel();
            toolbar.setTranslationY(0);
        }

        public final void animateShowToolbar() {
            toolbar.animate()
                    .translationY(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(Resources.getSystem().getInteger(android.R.integer.config_shortAnimTime));
        }

        public final void animateHideToolbar() {
            toolbar.animate()
                    .translationY(-toolbar.getHeight())
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(Resources.getSystem().getInteger(android.R.integer.config_shortAnimTime));
        }

        @Override
        public final void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && !AppStateUtils.isItemDeletionState) {
                if (scrollingOffset > 0) {
                    if (verticalOffset > toolbar.getHeight()) {
                        animateHideToolbar();
                    } else {
                        animateShowToolbar();
                    }
                } else if (scrollingOffset < 0) {
                    if (toolbar.getTranslationY() < toolbar.getHeight() * -0.6 && verticalOffset > toolbar.getHeight()) {
                        animateHideToolbar();
                    } else {
                        animateShowToolbar();
                    }
                }
            }
        }

        @Override
        public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!AppStateUtils.isItemDeletionState) {
                verticalOffset = recyclerView.computeVerticalScrollOffset();
                scrollingOffset = dy;
                int toolbarYOffset = (int) (dy - toolbar.getTranslationY());

                toolbar.animate().cancel();
                if (scrollingOffset > 0) {
                    if (toolbarYOffset < toolbar.getHeight()) {
                        toolbar.setTranslationY(-toolbarYOffset);
                    } else {
                        toolbar.setTranslationY(-toolbar.getHeight());
                    }
                } else if (scrollingOffset < 0) {
                    if (toolbarYOffset < 0) {
                        toolbar.setTranslationY(0);
                    } else {
                        toolbar.setTranslationY(-toolbarYOffset);
                    }
                }
            }
        }
    }

    public static void updateItemDeletionStateIcons(MainActivity activity, Menu menu, Course course, boolean itemDeletionState) {
        if (itemDeletionState) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
            menu.findItem(R.id.action_add_item).setVisible(false);
            menu.findItem(R.id.action_delete_item).setVisible(true);
        } else {
            if (course != null && course.isWeighted()) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_weight_grey600_24dp);
            } else {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
            menu.findItem(R.id.action_add_item).setVisible(true);
            menu.findItem(R.id.action_delete_item).setVisible(false);
        }
    }

    private ToolbarUtils() {
    }
}
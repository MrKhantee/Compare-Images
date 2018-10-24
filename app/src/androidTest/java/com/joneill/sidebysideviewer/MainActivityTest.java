package com.joneill.sidebysideviewer;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.joneill.sidebysideviewer.image.CompareContract;
import com.joneill.sidebysideviewer.image.CompareFragment;
import com.joneill.sidebysideviewer.image.ComparePresenter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.joneill.sidebysideviewer.ImageViewHasDrawableMatcher.hasDrawable;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

/**
 * Created by josep on 2/19/2017.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> mNotesActivityTestRule =
            new IntentsTestRule<MainActivity>(MainActivity.class);

    @Mock
    private CompareContract.View mCompareView;

    private ComparePresenter mComparePresenter;

    @Before
    public void setupNotesPresenter() {
        MockitoAnnotations.initMocks(this);

        mComparePresenter = new ComparePresenter(mCompareView);
    }


    @Test
    public void hideMessageFromScreen() throws Exception {
        // When we add an image
        mComparePresenter.imageAvailable(Uri.parse(""));
        // Check that hideMessage() is called
        verify(mCompareView).hideMessage();
    }

    @Test
    public void addImageToNote_ShowsThumbnailInUi() {
        Instrumentation.ActivityResult result = createImageSelectionActivityResultStub();
        intending((hasAction(equalTo(Intent.ACTION_GET_CONTENT)))).respondWith(result);

        onView(withId(R.id.fab_add_image)).perform(click());

        onView(withRecyclerView(R.id.images_list).atPositionOnView(0, R.id.image_item_bitmap))
                .check(matches(allOf(
                        hasDrawable(),
                        isDisplayed())));
  }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    private Instrumentation.ActivityResult createImageSelectionActivityResultStub() {
        // Create the ActivityResult, with a null Intent since we do not want to return any data
        // back to the Activity.
        return new Instrumentation.ActivityResult(Activity.RESULT_OK, null);
    }

}

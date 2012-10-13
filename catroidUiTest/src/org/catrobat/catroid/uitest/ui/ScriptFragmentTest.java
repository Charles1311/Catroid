/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.ui;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class ScriptFragmentTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private List<Brick> brickListToCheck;

	public ScriptFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		brickListToCheck = UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptTabActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		solo.setActivityOrientation(Solo.PORTRAIT);
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testCreateNewBrickButton() {
		int brickCountInView = solo.getCurrentListViews().get(0).getCount();
		int brickCountInList = brickListToCheck.size();

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.clickOnText(solo.getString(R.string.brick_when_started));
		solo.sleep(100);

		assertTrue("Wait brick is not in List", solo.searchText(solo.getString(R.string.brick_wait)));

		assertEquals("Brick count in list view not correct", brickCountInView + 1, solo.getCurrentListViews().get(0)
				.getCount());
		assertEquals("Brick count in brick list not correct", brickCountInList + 1, ProjectManager.getInstance()
				.getCurrentScript().getBrickList().size());
	}

	public void testBrickCategoryDialog() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

		if (!pref.getBoolean("setting_mindstorm_bricks", false)) {
			UiTestUtils.goToHomeActivity(solo.getCurrentActivity());
			solo.clickOnMenuItem(solo.getString(R.string.main_menu_settings));
			solo.clickOnText(solo.getString(R.string.pref_enable_ms_bricks));
			solo.goBack();
			solo.clickOnText(solo.getString(R.string.main_menu_continue));
			solo.clickOnText(solo.getString(R.string.background));
		}

		UiTestUtils.clickOnActionBar(solo, R.id.menu_add);
		String categorySoundLabel = solo.getString(R.string.category_sound);
		String categoryLegoNXTLabel = solo.getString(R.string.category_lego_nxt);
		String categoryControlLabel = solo.getString(R.string.category_control);
		String categoryLooksLabel = solo.getString(R.string.category_looks);
		String categoryMotionLabel = solo.getString(R.string.category_motion);

		// Test if all Categories are present
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categoryMotionLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categoryLooksLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categorySoundLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryControlLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryLegoNXTLabel));

		// Test if the correct category opens when clicked
		String brickPlaceAtText = solo.getString(R.string.brick_place_at);
		String brickSetCostume = solo.getString(R.string.brick_set_costume);
		String brickPlaySound = solo.getString(R.string.brick_play_sound);
		String brickWhenStarted = solo.getString(R.string.brick_when_started);

		solo.clickOnText(solo.getString(R.string.category_motion));
		assertTrue("AddBrickDialog was not opened after selecting a category", solo.searchText(brickPlaceAtText));
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.category_looks));
		assertTrue("AddBrickDialog was not opened after selecting a category", solo.searchText(brickSetCostume));
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.category_sound));
		assertTrue("AddBrickDialog was not opened after selecting a category", solo.searchText(brickPlaySound));
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.category_control));
		assertTrue("AddBrickDialog was not opened after selecting a category", solo.searchText(brickWhenStarted));
	}

	public void testSimpleDragNDrop() {
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo);
		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 6);

		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(4), 10, yPositionList.get(2), 20);
		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();

		assertEquals("Brick count not equal before and after dragging & dropping", brickListToCheck.size(),
				brickList.size());
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(0), brickList.get(0));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(3), brickList.get(1));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(1), brickList.get(2));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(2), brickList.get(3));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(4), brickList.get(4));
	}

	public void testDeleteItem() {
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo);
		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 6);

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int displayWidth = display.getWidth();

		UiTestUtils.longClickAndDrag(solo, 30, yPositionList.get(2), displayWidth, yPositionList.get(2), 40);
		solo.sleep(1000);
		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();

		assertEquals("This brick shouldn't be deleted due TrashView does not exist", brickListToCheck.size(),
				brickList.size());

		solo.clickOnScreen(20, yPositionList.get(2));
		if (!solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick), 0, 5000)) {
			fail("Text not shown in 5 secs!");
		}
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		if (!solo.waitForView(ListView.class, 0, 5000)) {
			fail("Dialog does not close in 5 sec!");
		}
		brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();

		assertEquals("Wrong size of BrickList - one item should be removed", brickListToCheck.size() - 1,
				brickList.size());

		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(0), brickList.get(0));
		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(2), brickList.get(1));
		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(3), brickList.get(2));
		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(4), brickList.get(3));
	}

	public void testBackgroundBricks() {
		String currentProject = solo.getString(R.string.main_menu_continue);
		String background = solo.getString(R.string.background);
		String categoryLooks = solo.getString(R.string.category_looks);
		String categoryMotion = solo.getString(R.string.category_motion);
		String setBackground = solo.getString(R.string.brick_set_background);
		String nextBackground = solo.getString(R.string.brick_next_background);
		String comeToFront = solo.getString(R.string.brick_come_to_front);
		String goNStepsBack = solo.getString(R.string.brick_go_back_layers);

		UiTestUtils.goToHomeActivity(solo.getCurrentActivity());

		solo.clickOnText(currentProject);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText(background);
		UiTestUtils.clickOnActionBar(solo, R.id.menu_add);
		solo.clickOnText(categoryLooks);
		assertTrue("SetCostumeBrick was not renamed for background sprite", solo.searchText(setBackground));
		solo.clickOnText(setBackground);
		solo.clickOnText(solo.getString(R.string.brick_when_started));
		assertTrue("SetCostumeBrick was not renamed for background sprite", solo.searchText(setBackground));
		UiTestUtils.clickOnActionBar(solo, R.id.menu_add);
		solo.clickOnText(categoryLooks);
		assertTrue("NextCostumeBrick was not renamed for background sprite", solo.searchText(nextBackground));
		solo.clickOnText(nextBackground);
		solo.clickOnText(solo.getString(R.string.brick_when_started));
		assertTrue("NextCostumeBrick was not renamed for background sprite", solo.searchText(nextBackground));

		UiTestUtils.clickOnActionBar(solo, R.id.menu_add);
		solo.clickOnText(categoryMotion);
		assertFalse("ComeToFrontBrick is in the brick list!", solo.searchText(comeToFront));
		assertFalse("GoNStepsBackBrick is in the brick list!", solo.searchText(goNStepsBack));
	}
}

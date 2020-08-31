package edu.wright.gameoflife2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import java.util.HashMap;

/**
 * This is the main Android Activity for the application. It interacts with the GameOfLifeModel and
 * one or more Android.View instances to provide a user interface.
 *
 * Note: to enable assertions, make sure the application is compiled with DEBUG enabled. In
 * Android Studio, use the Build->Edit Build Types...  menu. Select the "Debug" build type and make
 * sure the "debuggable" option is set to true.
 *
 * @author Erik M. Buck
 * @version %G%
 */
public class GameOfLifeActivity extends AppCompatActivity {

    // These are traditional start configurations for Game of Life
    private static final int[][] gliderCellPositions = {{1, 0}, {2, 1}, {2, 2}, {1, 2}, {0, 2},};
    private static final int smallExplosionsCellPositions[][] = {{0, 1}, {0, 2}, {1, 0}, {1, 1}, {1, 3}, {2, 1}, {2, 2},};
    private static final int explosionsCellPositions[][] = {{0, 0}, {0, 1}, {0, 2}, {0, 3}, {0, 4}, {2, 0}, {2, 4}, {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4},};
    private static final int fishCellPositions[][] = {{0, 1}, {0, 3}, {1, 0}, {2, 0}, {3, 0}, {3, 3}, {4, 0}, {4, 1}, {4, 2}};
    private static final int tenInARowCellPositions[][] = {{-4, 0}, {-3, 0}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0},};
    private static final int pumpCellPositions[][] = {{0, 3}, {0, 4}, {0, 5}, {1, 0}, {1, 1}, {1, 5}, {2, 0}, {2, 1}, {2, 2}, {2, 3}, {2, 4}, {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4}, {5, 0}, {5, 1}, {5, 5}, {6, 3}, {6, 4}, {6, 5},};
    private static final int shooterCellPositions[][] = {{0, 2}, {0, 3}, {1, 2}, {1, 3}, {8, 3}, {8, 4}, {9, 2}, {9, 4}, {10, 2}, {10, 3}, {16, 4}, {16, 5}, {16, 6}, {17, 4}, {18, 5}, {22, 1}, {22, 2}, {23, 0}, {23, 2}, {24, 0}, {24, 1}, {24, 12}, {24, 13}, {25, 12}, {25, 14}, {26, 12}, {34, 0}, {34, 1}, {35, 0}, {35, 1}, {35, 7}, {35, 8}, {35, 9}, {36, 7}, {37, 8},};
    private static final int clearCellPositions[][] = {};
    private GameOfLifeView mGameView;

    // This provides a convenient initial positions look-up by resource id
    private static HashMap<Integer, int[][]> initialCellPositions() {
        final HashMap<Integer, int[][]> map = new HashMap<>();
        map.put(R.id.action_clear, clearCellPositions);
        map.put(R.id.action_glider, gliderCellPositions);
        map.put(R.id.action_small_explosion, smallExplosionsCellPositions);
        map.put(R.id.action_explosion, explosionsCellPositions);
        map.put(R.id.action_fish, fishCellPositions);
        map.put(R.id.action_ten_in_a_row, tenInARowCellPositions);
        map.put(R.id.action_pump, pumpCellPositions);
        map.put(R.id.action_shooter, shooterCellPositions);
        return map;
    }

    /**
     * Set the model to be displayed via the user interface. model is not mutated by this method.
     * @param model The model to display (cannot be null)
     */
    private void setModel(GameOfLifeModel model) {
        mGameView.setModel(model);
        mGameView.center();
    }

    /**
     * This is a Template Method: See https://en.wikipedia.org/wiki/Template_method_pattern
     * This method is called automatically by Android when Android wants and Activity to start.
     * This implementation initializes the Activity's GameOfLifeView by "inflating" it from
     * resources.  This implementation then creates and initializes a GameOfLifeModel instance,
     * and then makes the new instance the GameOfLifeView's model to display.
     *
     * When this method returns, the Activity's GameOfLifeView and that view's GameOfLifeModel
     * are guaranteed to be non-null.
     *
     * This method may load and initialize user interface elements besides just the GameOfLifeView,
     * but such user interface elements are typically defined in resource files separate from this
     * class, and this class makes no assurances regarding user interface elements besides the
     * GameOfLifeView.
     */
    @Override
    protected void onStart() {
        super.onStart();

        if(BuildConfig.DEBUG) Log.d("Application wide:", "Assertions are enabled.");

        setContentView(R.layout.activity_game_of_life);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mGameView = (GameOfLifeView) findViewById(R.id.gameoflife);

        final SeekBar speedSlider = (SeekBar) findViewById(R.id.speed_slider);
        if (null != speedSlider) {
            speedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mGameView.setUpdatePeriodMs(progress);
                }

                public void onStartTrackingTouch(SeekBar seekBar) {}
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (null != fab) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGameView.toggleIsRunning();
                    if (mGameView.getIsRunning()) {
                        fab.setImageResource(android.R.drawable.ic_media_pause);
                    } else {
                        fab.setImageResource(android.R.drawable.ic_media_play);
                    }
                }
            });

        }
        setModel(new GameOfLifeModel(tenInARowCellPositions));
    }

    /**
     * This is a Template Method: See https://en.wikipedia.org/wiki/Template_method_pattern
     * This method is called automatically by Android when Android wants and Activity to pause.
     * This implementation stops the Activity's GameOfLifeView running if it is running when
     * this method is called. This method does not mutate the Activity's GameOfLifeView.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mGameView.getIsRunning())  mGameView.toggleIsRunning();
    }

    /**
     * This is a Template Method: See https://en.wikipedia.org/wiki/Template_method_pattern
     * This method is called automatically by Android when Android wants to create an "options" menu
     * for the Activity.
     * This implementation initializes menu by calling Android's getMenuInflater().inflate()
     * method.
     * This method does not mutate the Activity's GameOfLifeView. Whether or not the view is
     * "running" is not affected by this method: If the view is running when this method is called,
     * the view will still be running but possibly using a different model when this method returns.
     *
     * @param menu The Android provided menu to initialize.
     * @return true if and only if menu is successfully initialized.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_of_life, menu);
        return true;
    }

    /**
     * This is a Template Method: See https://en.wikipedia.org/wiki/Template_method_pattern
     * This method is called automatically by Android when a menu item is selected by a user.
     * This implementation checks whether the selected menu item id is one that corresponds
     * to one of the available GameOfLife initial position configurations. If so, this method calls
     * setModel() passing a new GameOfLifeModel initialized from the initial position configuration
     * and then returns true.
     * Otherwise, this method returns teh result of calling super.onOptionsItemSelected(item).
     *
     * This method potentially mutates the Activity's GameOfLifeView by replacing the
     * GameOfLifeModel displayed by the view to a new GameOfLifeModel instance. In all cases, when
     * this method returns, the Activity's GameOfLifeView and GameOfLifeModel are non-null.
     *
     * @param item The item selected
     * @return true if and only if item selection was correctly handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (GameOfLifeActivity.initialCellPositions().containsKey(id)) {
            setModel(new GameOfLifeModel(GameOfLifeActivity.initialCellPositions().get(id)));
            return true;
        }
        boolean result = super.onOptionsItemSelected(item);
        return result;
    }
}
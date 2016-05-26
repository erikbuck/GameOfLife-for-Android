package com.wsu.erikbuck.gameoflife;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import java.util.HashMap;

import static junit.framework.Assert.assertTrue;

/**
 * This is the main Activity for the application. It interacts with the GameOfLifeModel and one or
 * more Android.View instances.
 *
 * Note: to enable assertions, make sure the application is compiled with DEBUG enabled. In
 * Android Studio, use the Build->Edit Build Types...  menu. Select the "Debug" build type and make
 * sure the "debuggable" option is set to true.
 *
 * @author Erik M. Buck
 * @version %G%
 */
public class GameOfLifeActivity extends AppCompatActivity {

    private static final int[][] gliderCellPositions = {{1, 0}, {2, 1}, {2, 2}, {1, 2}, {0, 2},};
    private static final int smallExplosionsCellPositions[][] = {{0, 1}, {0, 2}, {1, 0}, {1, 1}, {1, 3}, {2, 1}, {2, 2},};
    private static final int explosionsCellPositions[][] = {{0, 0}, {0, 1}, {0, 2}, {0, 3}, {0, 4}, {2, 0}, {2, 4}, {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4},};
    private static final int fishCellPositions[][] = {{0, 1}, {0, 3}, {1, 0}, {2, 0}, {3, 0}, {3, 3}, {4, 0}, {4, 1}, {4, 2}};
    private static final int tenInARowCellPositions[][] = {{-4, 0}, {-3, 0}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0},};
    private static final int pumpCellPositions[][] = {{0, 3}, {0, 4}, {0, 5}, {1, 0}, {1, 1}, {1, 5}, {2, 0}, {2, 1}, {2, 2}, {2, 3}, {2, 4}, {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4}, {5, 0}, {5, 1}, {5, 5}, {6, 3}, {6, 4}, {6, 5},};
    private static final int shooterCellPositions[][] = {{0, 2}, {0, 3}, {1, 2}, {1, 3}, {8, 3}, {8, 4}, {9, 2}, {9, 4}, {10, 2}, {10, 3}, {16, 4}, {16, 5}, {16, 6}, {17, 4}, {18, 5}, {22, 1}, {22, 2}, {23, 0}, {23, 2}, {24, 0}, {24, 1}, {24, 12}, {24, 13}, {25, 12}, {25, 14}, {26, 12}, {34, 0}, {34, 1}, {35, 0}, {35, 1}, {35, 7}, {35, 8}, {35, 9}, {36, 7}, {37, 8},};
    private static final int clearCellPositions[][] = {};
    private GameOfLifeView mGameView;

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
     *
     * @param model The model to display (cannot be null)
     */
    private void setModel(GameOfLifeModel model) {
        if(BuildConfig.DEBUG) assertTrue(null != model);
        if(BuildConfig.DEBUG) assertTrue(null != mGameView);

        mGameView.setModel(model);
        mGameView.center();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(BuildConfig.DEBUG) Log.d("Application wide:", "Assertions are enabled.");

        setContentView(R.layout.activity_game_of_life);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(BuildConfig.DEBUG) assertTrue(null != toolbar);

        setSupportActionBar(toolbar);

        mGameView = (GameOfLifeView) findViewById(R.id.gameoflife);
        if(BuildConfig.DEBUG) assertTrue(null != mGameView);

        final SeekBar speedSlider = (SeekBar) findViewById(R.id.speed_slider);
        if (null != speedSlider) {
            speedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(BuildConfig.DEBUG) assertTrue(null != seekBar);
                    mGameView.setUpdatePeriodMs(progress);
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (null != fab) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(BuildConfig.DEBUG) assertTrue(null != mGameView);
                    // if(BuildConfig.DEBUG) Assert.assertTrue(null != fab); Java lint assures ALWAYS TRUE

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

        if(BuildConfig.DEBUG) assertTrue(null != mGameView);
        if(BuildConfig.DEBUG) assertTrue(null != mGameView.getModel());
    }

    @Override
    protected void onPause() {
        if(BuildConfig.DEBUG) assertTrue(null != mGameView);
        super.onPause();
        if (mGameView.getIsRunning()) {
            mGameView.toggleIsRunning();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(BuildConfig.DEBUG) assertTrue(null != menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_of_life, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(BuildConfig.DEBUG) assertTrue(null != item);
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (GameOfLifeActivity.initialCellPositions().containsKey(id)) {
            setModel(new GameOfLifeModel(GameOfLifeActivity.initialCellPositions().get(id)));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

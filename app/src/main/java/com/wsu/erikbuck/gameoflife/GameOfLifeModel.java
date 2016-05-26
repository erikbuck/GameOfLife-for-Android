package com.wsu.erikbuck.gameoflife;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import static junit.framework.Assert.assertTrue;


/**
 * This class encapsulates an arbitrary number of cells which apply the logic (rules) for the
 * Game of Life documented at http://www.bitstorm.org/gameoflife/. Cells conceptually exist in a
 * regular 2D grid using signed integer coordinates to identify each grid position that may be
 * occupied by a cell. At all times, there is at most one cell at each grid position.
 * <p/>
 * This implementation supports an "infinite" game area limited only by the use of integer cell
 * coordinates and the amount of available memory. This implementation does not use concurrency,
 * but the rules for evaluating the presence of neighbors simulate simultaneous evaluation for
 * every cell. In other words, upon each update of the game, all cells evaluate the same
 * environment prior to any cells modifying the environment. As a result, the order in which cells
 * evaluate the environment is immaterial.
 * <p/>
 * This implementation has no dependencies on the way a Game of Life model may or may not be
 * displayed to users. After construction, call the update() method periodically to update the game
 * state. The frequency of calls to update() does not affect game logic (rules).
 *
 * @author Erik M. Buck
 * @version %G%
 */
public class GameOfLifeModel {
    private Hashtable<CellCoordinate, LifeCell> mCells;

    // This inner class exists only to enable pre and post condition verification.
    class DataCopyHashtable {
        private final Hashtable<CellCoordinate, LifeCell> tableCopy;

        DataCopyHashtable() {
            //noinspection unchecked
            tableCopy = (Hashtable<CellCoordinate, LifeCell>)mCells.clone();
        }

        boolean doesMatch(Hashtable<CellCoordinate, LifeCell> table) {
            return tableCopy.equals(table);
        }
    }

    // This inner class exists only to enable pre and post condition verification.
    class DataCopyPositions {
        private final int[][] arrayCopy;

        DataCopyPositions(int[][] positions) {
            arrayCopy = positions.clone();
        }

        boolean doesMatch(int[][] array) {
            return Arrays.equals(array, arrayCopy);
        }

        boolean everythingIsInTable(Hashtable<CellCoordinate, LifeCell> table) {
            for (int coordinates[] : arrayCopy) {
                if (!table.containsKey(new CellCoordinate(coordinates))) {
                    return false;
                }
            }
            return true;
        }

        boolean containsEverythingInTable(Hashtable<CellCoordinate, LifeCell> table) {
            Set<CellCoordinate> set = new HashSet<>();
            for (int coordinates[] : arrayCopy) {
                set.add(new CellCoordinate(coordinates) );
            }

            return set.equals(table.keySet());
        }
    }

    /**
     * Constructs a GameOfLifeModel with cells at all positions in initialPositions.
     *
     * @param initialPositions an array of arrays of integer coordinates. Each arrays of integer
     *                         coordinates must contain exactly two integers.
     */
    @SuppressLint("assert")
    GameOfLifeModel(final int initialPositions[][]) {
        DataCopyPositions copy = null; // Pointless local variable only used to check post-conditions
        // Always succeeds; has side effect of saving a copy of array
        if(BuildConfig.DEBUG) assertTrue((copy = new DataCopyPositions(initialPositions)) != null);

        /////////////////////////////////////////////////////////////////
        /// Begin actual real implementation of method
        this.mCells = new Hashtable<>();

        for (int coordinates[] : initialPositions) {
            if(BuildConfig.DEBUG) assertTrue(2 == coordinates.length);
            this.spawnCellAt(new CellCoordinate(coordinates));
        }
        /// End actual real implementation of method
        /////////////////////////////////////////////////////////////////

        if(BuildConfig.DEBUG) assertTrue(null != mCells);
        if(BuildConfig.DEBUG) assertTrue(copy.doesMatch(initialPositions)); // initialPositions was not mutated
        if(BuildConfig.DEBUG) assertTrue(copy.everythingIsInTable(mCells)); // Every position in copy is in mCells
        if(BuildConfig.DEBUG) assertTrue(copy.containsEverythingInTable(mCells)); // Every position in mCells is in copy
    }

    /**
     * @param pos a position in the game grid (cannot be null)
     * @return true if there is a cell at pos and false otherwise
     */
    private boolean containsCellAt(CellCoordinate pos) {
        if(BuildConfig.DEBUG) assertTrue(null != pos);
        if(BuildConfig.DEBUG) assertTrue(null != mCells);

        DataCopyHashtable copy = null; // Pointless local variable only used to check post-conditions
        if(BuildConfig.DEBUG) assertTrue((copy = new DataCopyHashtable()) != null); // Always succeeds; has side effect of saving a copy of array

        /////////////////////////////////////////////////////////////////
        /// Begin actual real implementation of method
        boolean result = mCells.containsKey(pos);
        /// End actual real implementation of method
        /////////////////////////////////////////////////////////////////

        if(BuildConfig.DEBUG) assertTrue(null != mCells);
        if(BuildConfig.DEBUG) assertTrue(copy.doesMatch(mCells)); // mCells was not mutated
        return result;
    }

    /**
     * Returns the cell at pos if there is one and otherwise returns null.
     *
     * @param pos a position in the game grid (cannot be null)
     * @return the cell at pos or null if there is no cell at pos
     */
    private LifeCell getCellAt(CellCoordinate pos) {
        if(BuildConfig.DEBUG) assertTrue(null != pos);
        if(BuildConfig.DEBUG) assertTrue(null != mCells);

        return mCells.get(pos);
    }

    /**
     * Inserts a new cell into the game at pos. It is an error to spawn a cell at coordinates
     * that are already occupied by a cell unless the already existing cell has status Spawning.
     * In other words, replacing a Spawning cell with another Spawning cell is harmless, but any
     * other replacements are errors.
     *
     * @param pos the grid coordinates for the new cell. (cannot be null)
     */
    @SuppressLint("assert")
    private void spawnCellAt(final CellCoordinate pos) {
        if(BuildConfig.DEBUG) assertTrue(null != mCells);
        if(BuildConfig.DEBUG) assertTrue(null != pos);
        if(BuildConfig.DEBUG) assertTrue(!mCells.containsKey(pos) || Status.Spawning == mCells.get(pos).status);

        LifeCell newCell = new LifeCell(pos);
        mCells.put(pos, newCell);

        if(BuildConfig.DEBUG) assertTrue(null != mCells);
        if(BuildConfig.DEBUG) assertTrue(mCells.containsKey(pos) && Status.Spawning == mCells.get(pos).status);
    }

    /**
     * Update the state of all encapsulated cells. As each cell is updated, the cell applies the
     * logic (rules) of the Game of Life based on the presence or absence of cells in adjacent grid
     * positions to the cell being updated. Cells change their respective internal states, and
     * may call the private spawnCellAt() of this class.
     */
    @SuppressLint("assert")
    public void update() {
        if(BuildConfig.DEBUG) assertTrue(null != mCells);

        Hashtable<CellCoordinate, LifeCell> newTable = new Hashtable<>();

        // Convert Spawning cells into Alive cells and remove Dead cells from game grid
        for (LifeCell cell : mCells.values()) {
            if (cell.status == Status.Dead) {
                if(BuildConfig.DEBUG) assertTrue(!newTable.containsKey(cell.position));
            } else {
                cell.status = Status.Alive;
                newTable.put(cell.position, cell);
            }
        }

        mCells = newTable;

        // Update all the cells
        for (LifeCell cell : new ArrayList<>(mCells.values())) {
            if(BuildConfig.DEBUG) assertTrue(mCells.containsKey(cell.position));
            cell.update(this);
        }

        if(BuildConfig.DEBUG) assertTrue(null != mCells);
    }

    /**
     * @return A Set view of the CellCoordinates occupied by cells. Note: CellCoordinate instances
     * are immutable. The position of a cell cannot be changed once the cell is constructed. There
     * is no way to mutate cells using the Set returned by this method.
     */
    public Collection<CellCoordinate> getPositions() {
        return this.mCells.keySet();
    }

    /**
     * These are the states a LifeCell instance can be in.
     */
    private enum Status {
        Alive, Spawning, Dead
    }

    /**
     * This class encapsulates the coordinates of a cell in the Game of Life. Once
     * constructed, instances of this class are immutable.
     */
    public class CellCoordinate {
        private final int x;
        private final int y;

        /**
         * Constructs a new CellCoordinate with the specified x and y coordinates.
         *
         * @param x the specified x coordinate
         * @param y the specified y coordinate
         */
        public CellCoordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Constructs a new CellCoordinate with x equal to xy[0] and y equal to xy[1].
         *
         * @param xy an array of exactly 2 integers.
         */
        @SuppressLint("assert")
        public CellCoordinate(int xy[]) {
            if(BuildConfig.DEBUG) assertTrue(2 == xy.length);

            this.x = xy[0];
            this.y = xy[1];
        }

        /**
         * Constructs a new CellCoordinate with x equal to original.x + dx and y equal to
         * original.y + dy.
         *
         * @param original A CellCoordinate that (cannot be null)
         * @param dx       arbitrary delta added to original.x
         * @param dy       arbitrary delta added to original.y
         */
        public CellCoordinate(final CellCoordinate original, int dx, int dy) {
            if(BuildConfig.DEBUG) assertTrue(null != original);

            this.x = original.x + dx;
            this.y = original.y + dy;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        /**
         * @return Returns a string representation of the object.
         */
        @Override
        public String toString() {
            return "{" + x + ", " + y + "} ";
        }

        /**
         * @return Returns a hash code value for the object.
         */
        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }

        /**
         * Indicates whether some other object is "equal to" this one. Note: It is required that
         * any two objects that are equal return the same value from hashCode().
         *
         * @param other the object to compare to the receiver.
         * @return true if the receiver and other are equal to each other and false otherwise.
         */
        @SuppressLint("assert")
        @Override
        public boolean equals(Object other) {
            boolean result = (other != null &&
                    getClass() == other.getClass() &&
                    other.toString().equals(this.toString()));

            if(BuildConfig.DEBUG) assertTrue(!result || hashCode() == other.hashCode());
            return result;
        }
    }

    /**
     * The LifeCell class encapsulates a cell in the Game of Life. Once constructed, a cell's
     * position coordinates are immutable i.e. the cell does not move around. Cells progress
     * through a sequence of states represented by the status property. Status always begins
     * as Status.Spawning, changes to Status.Alive, and under appropriate conditions the status
     * changes to Status.Dead. The state changes always occur in that order, but the amount of
     * time any LifeCell instance spends in any of the states should not be assumed.
     */
    private class LifeCell {
        private final CellCoordinate position;
        /**
         * < the cell's position in the game grid
         */
        private Status status; /**< the cell's status */

        /**
         * Constructs a new LifeCell with game grid coordinates specified and status Spawning.
         *
         * @param position game grid coordinates for the new cell
         */
        public LifeCell(final CellCoordinate position) {
            if(BuildConfig.DEBUG) assertTrue(null != position);

            this.position = new CellCoordinate(position.x, position.y);
            this.status = Status.Spawning;

            // if(BuildConfig.DEBUG) Assert.assertTrue(null != position); Java lint assures ALWAYS TRUE
        }

        /**
         * @return Returns a string representation of the object.
         */
        @Override
        public String toString() {
            return position.toString() + status;
        }

        /**
         * @return Returns a hash code value for the object.
         */
        @Override
        public int hashCode() {
            return position.hashCode();
        }

        /**
         * Indicates whether some other object is "equal to" this one. Note: It is required that
         * any two objects that are equal return the same value from hashCode().
         *
         * @param other the object to compare to teh receiver.
         * @return true if the receiver and other are equal to each other and false otherwise.
         */
        @SuppressLint("assert")
        @Override
        public boolean equals(Object other) {
            boolean result = (other != null &&
                    getClass() == other.getClass() &&
                    this.toString().equals(other.toString()));

            if(BuildConfig.DEBUG) assertTrue(!result || hashCode() == other.hashCode());
            return result;
        }

        private boolean isSpawning() {
            return this.status == Status.Spawning;
        }

        /**
         * @param pos a position in the game grid
         * @return an array of all positions considered adjacent to pos
         */
        private CellCoordinate[] getNeighbors(CellCoordinate pos) {
            if(BuildConfig.DEBUG) assertTrue(null != pos);

            return new CellCoordinate[]{
                    new CellCoordinate(pos, -1, -1),
                    new CellCoordinate(pos, 0, -1),
                    new CellCoordinate(pos, 1, -1),
                    new CellCoordinate(pos, -1, 0),
                    new CellCoordinate(pos, 1, 0),
                    new CellCoordinate(pos, -1, 1),
                    new CellCoordinate(pos, 0, 1),
                    new CellCoordinate(pos, 1, 1),
            };
        }

        /**
         * @param pos      a position in the game grid (cannot be null)
         * @param theModel object that encapsulates the game grid (cannot be null)
         * @return the number positions considered adjacent to pos in theModel that do not contain
         * spawning cells. 0 <= positions <= 8
         */
        @SuppressLint("assert")
        private int getCountOfNotSpawningNeighbors(final CellCoordinate pos, GameOfLifeModel theModel) {
            if(BuildConfig.DEBUG) assertTrue(null != pos);
            if(BuildConfig.DEBUG) assertTrue(null != theModel);

            CellCoordinate neighborPositions[] = getNeighbors(pos);

            int countOfNotSpawnedNeighbors = 0;
            for (CellCoordinate nextPos : neighborPositions) {
                if (theModel.containsCellAt(nextPos) && !theModel.getCellAt(nextPos).isSpawning()) {
                    countOfNotSpawnedNeighbors += 1;
                }
            }

            if(BuildConfig.DEBUG) assertTrue(0 <= countOfNotSpawnedNeighbors && 8 >= countOfNotSpawnedNeighbors);
            return countOfNotSpawnedNeighbors;
        }

        /**
         * Update the cell's state by applying the logic (rules) of the Game of Life based on the
         * cell's environment encapsulated by theModel. Specifically, theModel is used to identify
         * neighboring cells to the cell being updated. The spawnCellAt() method of theModel may be
         * called as a side effect.
         *
         * @param theModel The model to me evaluated by the cell so that the cell can (cannot be null)
         */
        @SuppressLint("assert")
        private void update(GameOfLifeModel theModel) {
            if(BuildConfig.DEBUG) assertTrue(null != theModel);
            if(BuildConfig.DEBUG) assertTrue(status == Status.Alive);
            if(BuildConfig.DEBUG) assertTrue(theModel.containsCellAt(position));

            CellCoordinate neighborPositions[] = getNeighbors(position);
            int countOfNotSpawnedNeighbors = getCountOfNotSpawningNeighbors(position, theModel);

            if (2 > countOfNotSpawnedNeighbors) {
                // Each cell with one or no neighbors dies, as if by solitude.
                this.status = Status.Dead;
            } else if (3 < countOfNotSpawnedNeighbors) {
                // Each cell with four or more neighbors dies, as if by overpopulation.
                this.status = Status.Dead;
            }

            // Each unpopulated cell with three neighbors becomes populated.
            for (CellCoordinate pos : neighborPositions) {
                if (!theModel.containsCellAt(pos)) {
                    // pos is unpopulated
                    if (3 == getCountOfNotSpawningNeighbors(pos, theModel)) {
                        // pos has exactly 3 neighbors who are not spawning
                        theModel.spawnCellAt(pos);
                    }
                }
            }
        }
    }
}

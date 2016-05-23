package com.wsu.erikbuck.gameoflife;

import java.util.Collection;
import java.util.Hashtable;

/**
 * Created by erik on 5/23/16.
 */
public class GameOfLifeModel {
    static class CellCoordinate {
        public int x;
        public int y;

        CellCoordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        CellCoordinate(CellCoordinate original, int dx, int dy) {
            this.x = original.x + dx;
            this.y = original.y + dy;
        }
    }
    static class Cell {
        public enum Status {
            Alive, Spawning, Dead
        }

        public CellCoordinate position;
        public Status status;

        Cell(CellCoordinate position) {
            this.position = position;
            this.status = Status.Spawning;
        }

        boolean isSpawning() {
            return  this.status == Status.Spawning;
        }

        int getCountOfNotSpawnedNeighbors(CellCoordinate pos, GameOfLifeModel theModel) {
            CellCoordinate neighborPositions[] = {
                    new CellCoordinate(pos, -1, -1),
                    new CellCoordinate(pos,  0, -1),
                    new CellCoordinate(pos,  1, -1),
                    new CellCoordinate(pos, -1,  0),
                    new CellCoordinate(pos,  1,  0),
                    new CellCoordinate(pos, -1,  1),
                    new CellCoordinate(pos,  0,  1),
                    new CellCoordinate(pos,  1,  1),
            };

            int countOfNotSpawnedNeighbors = 0;
            for(CellCoordinate nextPos : neighborPositions) {
                if(theModel.containsKey(nextPos) && !theModel.getCellAt(nextPos).isSpawning()) {
                    countOfNotSpawnedNeighbors += 1;
                }
            }

            return countOfNotSpawnedNeighbors;
        }

        void update(GameOfLifeModel theModel) {
            CellCoordinate neighborPositions[] = {
                    new CellCoordinate(this.position, -1, -1),
                    new CellCoordinate(this.position,  0, -1),
                    new CellCoordinate(this.position,  1, -1),
                    new CellCoordinate(this.position, -1,  0),
                    new CellCoordinate(this.position,  1,  0),
                    new CellCoordinate(this.position, -1,  1),
                    new CellCoordinate(this.position,  0,  1),
                    new CellCoordinate(this.position,  1,  1),
            };

            int countOfNotSpawnedNeighbors = getCountOfNotSpawnedNeighbors(this.position, theModel);

            if(2 > countOfNotSpawnedNeighbors) {
                // Each cell with one or no neighbors dies, as if by solitude.
                this.status = Status.Dead;
            }
            else if(3 < countOfNotSpawnedNeighbors)  {
                // Each cell with four or more neighbors dies, as if by overpopulation.
                this.status = Status.Dead;
            }
            // Each cell with two or three neighbors survives.

            // Each unpopulated cell with three neighbors becomes populated.
            for(CellCoordinate pos : neighborPositions) {
                if(!theModel.containsKey(pos)) {
                    // pos is unpopulated
                    if(3 == getCountOfNotSpawnedNeighbors(pos, theModel)) {
                        theModel.spawnCellAt(pos);
                    }
                }
            }
        }
    }

     private Hashtable<CellCoordinate, Cell> mCells;

    GameOfLifeModel() {
        this.mCells = new Hashtable<CellCoordinate, Cell>();
    }

    GameOfLifeModel(CellCoordinate initialPositions[]) {
        this.mCells = new Hashtable<CellCoordinate, Cell>();
        for (CellCoordinate initialPosition : initialPositions) {
            this.mCells.put(initialPosition, new Cell(initialPosition));
        }
    }

    private boolean containsKey(CellCoordinate pos) {
        return this.mCells.containsKey(pos);
    }

    private Cell getCellAt(CellCoordinate pos) {
        return this.mCells.get(pos);
    }

    private void spawnCellAt(CellCoordinate pos) {
        this.mCells.put(pos, new Cell(pos));
    }

    public void update() {
        for(Cell value : this.getCells()) {
            value.update(this);
        }

        for(Cell value : this.getCells()) {
            if(value.status == Cell.Status.Spawning) {
                value.status = Cell.Status.Alive;
            } else if(value.status == Cell.Status.Dead) {
                this.mCells.remove(value.position);
            }
        }
    }

    public Collection<Cell> getCells() {
        return this.mCells.values();
    }
}

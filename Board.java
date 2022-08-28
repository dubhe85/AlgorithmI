import java.io.Console;
import java.security.InvalidParameterException;
import java.util.Arrays;

import edu.princeton.cs.algs4.Queue;

public class Board {

    private final int[][] tilesItems;
    private int dimensionVal;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {

        if (tiles == null) {
            throw new NullPointerException("tiles param cannot be null.");
        }

        int numTotalItems = 0;
        for (int i = 0; i < tiles.length;i++) {
            numTotalItems += tiles[i].length;
        }

        if (numTotalItems < 2 || numTotalItems > 128) {
            throw new InvalidParameterException("tiles array param cannot be less than 2 or greater than 128.");
        }

        int totalNumTiles = tiles[0].length;
        totalNumTiles = totalNumTiles * totalNumTiles;
        if (totalNumTiles != numTotalItems) {
            throw new InvalidParameterException("Invalid amount of items in tiles array param.");
        }

        dimensionVal = (int)Math.sqrt(totalNumTiles);
        tilesItems = copyTilesArray(tiles);        
    }     
    
    private int[][] copyTilesArray(int[][] tilesArray) {
        
        int[][] newArray = new int[tilesArray.length][tilesArray.length];

        for (int i = 0; i < tilesArray.length; i++) {

            for (int j = 0; j < tilesArray[i].length; j++) {
                newArray[i][j] = tilesArray[i][j];
            }
        }

        return newArray;
    }
    
    
    // string representation of this board
    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append(dimensionVal + "\n");
        for (int i = 0; i < dimensionVal; i++) {
            for (int j = 0; j < dimensionVal; j++) {
                s.append(String.format("%2d ", tilesItems[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    
    // board dimension n
    public int dimension() {
        return dimensionVal;
    }
    
    // number of tiles out of place
    public int hamming() {
        
        int tilesWrongPos = 0;
        int tileReferenceVal = 1;        
        for(int i = 0; i < dimensionVal; i++) {
            for (int j = 0; j < dimensionVal; j++) {

                if (tilesItems[i][j] != tileReferenceVal) {
                    tilesWrongPos++;
                }

                tileReferenceVal++;
            }
        }

        if (tilesItems[dimensionVal - 1][dimensionVal - 1] == 0) {
            tilesWrongPos--;
        }

        return tilesWrongPos;
    }

    
    // sum of Manhattan distances between tiles and goal
    public int manhattan() {

        int[] distanceArray = new int[dimensionVal* dimensionVal];
        int currentPosition, goalPosition = 1;
        int totalManhattanDistance = 0;
        int manhattanDistance = 0;
        for (int i = 0; i < dimensionVal; i++) {

            for (int j = 0; j < dimensionVal; j++) {

                if (tilesItems[i][j] == 0) {
                    continue;
                }
                
                currentPosition = tilesItems[i][j];
                if (currentPosition != goalPosition) {
                    manhattanDistance = calculateManhattan(currentPosition, goalPosition);
                    distanceArray[currentPosition -1] = manhattanDistance;
                }
                goalPosition++;
            }
        }


        for (int i = 0; i < distanceArray.length; i++) {
            totalManhattanDistance += distanceArray[i];
        }

        return totalManhattanDistance;
    }

    private int calculateManhattan(int currentPosition, int goalPosition) {
        
        int currentTileRow = currentPosition / dimensionVal;
        int currentTileCol = currentPosition % dimensionVal;
        int goalTileRow = goalPosition / dimensionVal;
        int goalTileCol = goalPosition % dimensionVal;

        return Math.abs(currentTileRow - goalTileRow) + Math.abs(currentTileCol - goalTileCol);
    }

     
    private int lastItemColRowIndexesSum() {

        int lastIndexVal = dimensionVal + dimensionVal - 2;
        return lastIndexVal;
    }

    // is this board the goal board?
    public boolean isGoal() {

        boolean isGoalBoard = true;
        int sumLastItemColRowSum = lastItemColRowIndexesSum();
        int sumColRowIndexes;
        for (int i = 0; i < tilesItems.length; i++) {

            for (int j = 0; j < tilesItems[i].length; j++) {

                sumColRowIndexes = i + j;
                if (sumColRowIndexes != sumLastItemColRowSum && tilesItems[i][j] != sumColRowIndexes + 1) {
                    isGoalBoard = false;
                }
            }
        }

        return isGoalBoard;
    }

    // does this board equal y?
    public boolean equals(Object y) {

        if (this == y) return true;
        if (y == null) return false;
        if (this.getClass()  != y.getClass()) return false;
        Board that = (Board)y;
        if (!Arrays.deepEquals(this.tilesItems, that.tilesItems)) return false;
        return true;
    }

    private boolean validTileIndexes(int rowIndex, int colIndex) {

        boolean valid = true;

        if (rowIndex < 0 || rowIndex > dimensionVal - 1 ||
            colIndex < 0 || colIndex > dimensionVal - 1) {
                valid = false;
        }
        return valid;
    }

    private int[][] createTilesArrayPositionsInterchanged(int _blankRowIndex, int _blankColIndex, int _rowIndex, int _colIndex) {

        int[][] newArray = copyTilesArray(tilesItems);

        newArray[_blankRowIndex][_blankColIndex] = tilesItems[_rowIndex][_colIndex];
        newArray[_rowIndex][_colIndex] = 0;

        return newArray;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {

        Queue<Board> boardQueue = new Queue<Board>();

        int blankRowIndex = -1, blankColIndex = 1;
        boolean blankTileFound = false;
        for (int i = 0; i < tilesItems.length; i++) {

            for (int j = 0; j < tilesItems[i].length; j++) {
                
                if (tilesItems[i][j] == 0) {
                    
                    blankTileFound = true;
                    blankRowIndex = i;
                    blankColIndex = j;
                    break;
                }
            }

            if (blankTileFound) {
                break;
            }
        }
        
        int tempRowIndex  =-1, tempColIndex = -1;
        for (int k = 1; k <= 4; k++) {

            if (k == 1) {
                tempRowIndex = blankRowIndex - 1;
                tempColIndex = blankColIndex;
            }
            else if (k == 2) {
                tempRowIndex = blankRowIndex;
                tempColIndex = blankColIndex - 1;
            }
            else if (k == 3) {
                tempRowIndex = blankRowIndex;


                tempColIndex = blankColIndex + 1;
            }
            else if (k == 4) {
                tempRowIndex = blankRowIndex + 1;
                tempColIndex = blankColIndex;
            }

            // tempRowIndex and tempColIndex are inside dimension
            if (validTileIndexes(tempRowIndex, tempColIndex)) {
                int[][] newTilesArray = createTilesArrayPositionsInterchanged(blankRowIndex, blankColIndex, tempRowIndex, tempColIndex);
                Board newBoard = new Board(newTilesArray);
                boardQueue.enqueue(newBoard);
            }
        }

        return boardQueue;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {

        Board twinBoard = null;
        int[][] newTilesArray = null;
        int rowIndexFirstTile = -1, colIndexFirstTile = -1;
        int rowIndexSecondTile = -1, colIndexSecondTile = -1;
        boolean firstTileFound = false, secondTileFound = false;

        for (int i = 0; i < tilesItems.length; i++) {
            for (int j = 0; j < tilesItems[i].length; j++) {

                if (tilesItems[i][j] == 0) {
                    continue;
                }

                if (!firstTileFound) {
                    rowIndexFirstTile = i;
                    colIndexFirstTile = j;
                    firstTileFound = true;                    
                }
                else if (!secondTileFound) {
                    rowIndexSecondTile = i;
                    colIndexSecondTile = j;
                    secondTileFound = true;
                    break;
                }
            }

            if (firstTileFound && secondTileFound) {
                break;
            }
        }

        newTilesArray = copyTilesArray(tilesItems);

        // swap items
        int tempVal = newTilesArray[rowIndexFirstTile][rowIndexFirstTile];
        newTilesArray[rowIndexFirstTile][rowIndexFirstTile] = newTilesArray[rowIndexSecondTile][rowIndexSecondTile];
        newTilesArray[rowIndexSecondTile][rowIndexSecondTile] = tempVal;
        twinBoard = new Board(newTilesArray);

        return twinBoard;
    }

    // unit testing (not graded)
    public static void main(String[] args) {


        int[][] tiles = { {1, 2, 3},
                          {4, 5, 6},
                          {7, 8, 0} 
                        };
        Board boardItem = new Board(tiles); 
        
        // print Board
        String boardString = boardItem.toString();
        System.out.print(boardString);

        // get board dimension
        System.out.println("Calling dimension method: " + boardItem.dimension());

        // hamming
        System.out.println("Calling hamming method: " + boardItem.hamming());

        //  manhattan
        System.out.println("Calling manhattan method: " + boardItem.manhattan());

        // calculateManhattan() internal not need to test
        //System.out.println("Calling calculateManhattan method: " + boardItem.calculateManhattan());

        // isGoal
        System.out.println("Calling isGoal method: " + boardItem.isGoal());

        // equals
        int[][] secondTiles = { {7, 2, 3},
                                {4, 5, 6},
                                {1, 8, 0} 
                              };
        Board secondBoard = new Board(secondTiles);
        
        System.out.println("Calling equals method: " + boardItem.equals(secondBoard) + " expected to return false.");
                  
        int[][] thirdTiles = { {7, 2, 3},
                        {4, 5, 6},
                        {1, 8, 0} 
                              };
        Board thirdBoard = new Board(secondTiles);

        System.out.println("Calling equals method: " + boardItem.equals(thirdBoard) + " expected to return true.");

    }

}
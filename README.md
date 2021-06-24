# ComposeExamples

## Class File Viewer

This loads a class file into the app, on the left it shows the bytes with alternate 'sections' highlighted so you can see where one section ends and another begins. Sections are things like parts of the header, version etc, and entries in the constant pool. On the right it shows the header information, and, if you click on a constant pool entry on the left, also shows the details of the constant pool. I'd like to do this on a 'mouse over' event but at the moment there is no support for that (this is currently version 0.4.0 of Compose Desktop). The example also has an example of using a menu and it 'borrows' code from the 'official' Desktop Compose samples to add a splitter, this code is updated to version 0.4.0 (the official examples at the time of writing were out of date) and uses types like `LocalAppWindow` rather than `AppWindowAmbient` for example

## Sudoku Solver

You can use this in a number of ways, you can either type in the vales in the Sudoku cells, (click on the cell to activate it), or load a CSV file containing the Sudoku puzzle, examples of files are provided. You can either 'Run', or 'Animate' the solution. The animation runs slowly and so shows the puzzle being solved. This uses backtracking (I took the slgorithm from [here](https://furkankamaci.medium.com/algorithm-to-solve-a-sudoku-416a22711f9f)). The app also uses Kotlin 'Flow' to get the data from the iterative 'backtrack' method to update the grid while the puzzle is being solved

## Game of Life

Simple setup for 'Conway's Game of Life'
You can randomize the grid (this will turn on invert 10% of the squares), click a button to draw a 'glider' at a random location, run and pause the animation and reset the grid. Running is done from a co-routine, stopping the run simply cancels the job created when the coroutine is started, the coroutine has a call to `delay` so manages the cancellation
Click on grid to set the life cells,  (load standard shapes from buttons?), (pause and run buttons)
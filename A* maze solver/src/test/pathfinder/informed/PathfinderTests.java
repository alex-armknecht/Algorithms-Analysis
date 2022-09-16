package test.pathfinder.informed;

import static org.junit.Assert.*;
import main.pathfinder.informed.*;
import main.pathfinder.informed.MazeProblem.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import java.util.*;

/**
 * Unit tests for Maze Pathfinder. Tests include completeness, and
 * optimality, and runtime efficiency.
 */
public class PathfinderTests {
    
    // =================================================
    // Test Configuration
    // =================================================
    
    // Global timeout to prevent infinite loops from
    // crashing the test suite, plus, tests to make sure
    // you're not implementing anything too computationally
    // crazy
    @Rule
    public Timeout globalTimeout = Timeout.seconds(1);
    
    // Each time you pass a test, you get a point! Yay!
    // [!] Requires JUnit 4+ to run
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            passed++;
        }
    };
    
    // Grade record-keeping
    static int possible = 0, passed = 0;
    
    // the @Before method is run before every @Test
    @Before
    public void init () {
        possible++;
    }
    
    // Used for grading, reports the total number of tests
    // passed over the total possible
    @AfterClass
    public static void gradeReport () {
        System.out.println("============================");
        System.out.println("Tests Complete");
        System.out.println(passed + " / " + possible + " passed!");
        if ((1.0 * passed / possible) >= 0.9) {
            System.out.println("[!] Nice job!"); // Automated acclaim!
        }
        System.out.println("============================");
    }
    
    
    // =================================================
    // Unit Tests
    // =================================================
    
    /**
     * Constructs a MazeProblem around the given maze to ensure that
     * nothing funky is happening in the one that's being used to solve
     * the maze, then tests the given solution against it.
     * 
     * @param maze The maze to solve
     * @param solution The proposed solution to it (possibly null)
     * @return The MazeTestResult determining if it's a solution and the
     * total cost if so.
     */
    public MazeTestResult testSolution (String[] maze, ArrayList<String> solution) {
        MazeProblem prob = new MazeProblem(maze);
        return prob.testSolution(solution);
    }
    
    
    // Test cases with solutions
    // -------------------------------------------------
    @Test
    public void testPathfinder_t0() { 
        String[] maze = {
            "XXXXXXX",
            "XI...KX",
            "X.....X",
            "X.X.XGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        // Making this MazeProblemSolution object simply makes sure
        // any errors in your MazeProblem (especially those
        // potentially introduced by its testSolution method) do not
        // subject you to double jeopardy
        MazeTestResult result = testSolution(maze, solution);
        assertTrue(result.IS_SOLUTION); // Test that result is a solution
        assertEquals(6, result.COST);   // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t1() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.MMM.X",
            "X.XKXGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeTestResult result = testSolution(maze, solution);
        assertTrue(result.IS_SOLUTION);
        assertEquals(14, result.COST);
    }
    
    @Test
    public void testPathfinder_t2() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MMMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeTestResult result = testSolution(maze, solution);
        assertTrue(result.IS_SOLUTION);
        assertEquals(10, result.COST);
    }
    
    @Test
    public void testPathfinder_t3() {
        String[] maze = {
            "XXXKXXX",
            "X..IMMX",
            "X.XXXGX",
            "X.XXX.X",
            "X.G...X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeTestResult result = testSolution(maze, solution);
        assertTrue(result.IS_SOLUTION);
        assertEquals(8, result.COST);
    }
    
    @Test
    public void testPathfinder_t4() {
        String[] maze = {
            "XXXXXXX",
            "X....IX",
            "X..MXXX",
            "XGXKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeTestResult result = testSolution(maze, solution);
        assertTrue(result.IS_SOLUTION);
        assertEquals(12, result.COST);
    }
    
    @Test
    public void testPathfinder_t5() {
        String[] maze = {
            "XXXXXXX",
            "XIGGGGX",
            "XGGGGGX",
            "XGGGGKX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeTestResult result = testSolution(maze, solution);
        assertTrue(result.IS_SOLUTION);
        assertEquals(7, result.COST);
    }
    // Test cases *without* solutions
    // -------------------------------------------------
    @Test
    public void testPathfinder_nosoln_t0() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MXMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution
    }
    @Test
    public void testPathfinder_nosoln_t1() { 
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MXMGX",
            "X.X.X.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution
    }
    @Test
    public void testPathfinder_nosoln_t2() { 
        String[] maze = {
            "XXXXXXX",
            "XI.M..X",
            "X.MXMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution
    }
    @Test
    public void testPathfinder_nosoln_t3() { 
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.....X",
            "X.....X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution
    }
    
}

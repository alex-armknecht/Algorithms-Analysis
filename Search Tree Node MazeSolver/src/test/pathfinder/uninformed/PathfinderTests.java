package test.pathfinder.uninformed;

import main.pathfinder.uninformed.*;
import main.pathfinder.uninformed.MazeProblem.*;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;

import java.util.*;

/**
 * Unit tests for Maze Pathfinder. Tests include completeness and
 * optimality.
 */
public class PathfinderTests {

    // =================================================
    // Test Configuration
    // =================================================
    
    // Global timeout to prevent infinite loops from
    // crashing the test suite
    // [!] When using the debugger, comment out the next
    // 2 lines:
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
    
    // The @Before mmethod is run before every @Test
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
    // For grading purposes, every method and unit is 
    // weighted equally and totaled for the score.
    // The tests increase in difficulty such that the
    // basics are unlabeled and harder tiers are tagged
    // t0, t1, t2, t3, ... easier -> harder
    
    @Test
    public void testPathfinder_t0() {
        String[] maze = {
            "XXXX",
            "X.IX",
            "XG.X",
            "XXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        MazeTestResult result = prob.testSolution(solution);
        assertTrue(result.IS_SOLUTION); // Test that result is a solution
        assertEquals(2, result.COST);   // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t1() {
        String[] maze = {
            "XXXXXXX",
            "X.....X",
            "XIX.X.X",
            "XX.X..X",
            "XG....X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        MazeTestResult result = prob.testSolution(solution);
        assertTrue(result.IS_SOLUTION);
        assertEquals(12, result.COST);
    }
    
    @Test
    public void testPathfinder_t2() {
        String[] maze = {
            "XXXX",
            "XIGX",
            "XXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        MazeTestResult result = prob.testSolution(solution);
        assertTrue(result.IS_SOLUTION);
        assertEquals(1, result.COST);
    }
    
    @Test
    public void testPathfinder_t3() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "XXXXX.X",
            "X.....X",
            "X.XXXXX",
            "X....GX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        MazeTestResult result = prob.testSolution(solution);
        assertTrue(result.IS_SOLUTION);
        assertEquals(16, result.COST);
    }

}

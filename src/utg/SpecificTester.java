package utg;

import main.About;

/**
 * This type is intended for testing a specific component / functionality of the project.
 * Notice: using utg.GeneralTester loads the entire project which can be time and memory consuming.
 * This class becomes useful when testing: for instance how accurate is some computation returned by Dashboard?
 * or how exactly are some components rendered before loading them up with the entire project.
 * E.g, to see how the Preview window looks like, use something like:
 *  SwingUtilities.invokeLater(()-> new Preview(null).setVisible(true));
 *
 * In whatever case, developer is assumed to be working on that particular side of the project.
 *
 * Never rely on this class for mass-testing! It does not load any data into memory, thus exception-prone.
 */
public class SpecificTester {

    public static void main(String[] args) {
        new About().setVisible(true);
    }

}

package main;


/**
 * <h1>Interface ActivityAnswerer</h1>
 *
 * <p>All classes that answer activity of the body by representing it must implement this interface.</p>
 * <p>Notice this refers not to the bigButtons, and their consequences. Hence it mainly implies the home-panels.</p>
 */
interface ActivityAnswerer {

    /**
     * <p>Whenever this method is invoked, the calling-class should own the 'bodyLayer' in its own way.</p>
     * <p><i>The consequence of this must be the result of a home-panel click?</i></p>
     */
    void answerActivity();

}

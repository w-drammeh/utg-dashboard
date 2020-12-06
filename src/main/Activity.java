package main;


/**
 * All classes that answer activity of the body by representing it must implement this interface.
 */
interface Activity {

    /**
     * Whenever this method is invoked, the calling-class should own the 'bodyLayer' in its own way,
     * with possible pre- and, or post-tasks.
     * E.g: the main.AnalysisGenerator type answers by making pre-tasks;
     * while the main.TranscriptGenerator, post-tasks.
     * The consequence of this must not only be the result of a home-panel click,
     * as it traditionally use to be.
     * By convention, this call should physically be placed directly beneath the constructor.
     */
    void answerActivity();

}

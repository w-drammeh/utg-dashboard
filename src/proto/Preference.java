package proto;

/**
 * All classes of the proto package must implement the Preference interface even if component-modifications
 * are not compulsory on them - as the case of the KFontFactory which is abstract, relieving itself of
 * this duty.
 */
interface Preference {

    /**
     * The constructors of all implementors must also delegate to this method - setPreferences() -
     * to initialize them as Dashboard specific components.
     */
    void setPreferences();

}

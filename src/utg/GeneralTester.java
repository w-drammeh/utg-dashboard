package utg;

import main.*;

import javax.swing.*;

/**
 * This class runs Dashboard as if the user is already verified and it's intended for testing only!
 * This class simply jumps into the 'Build Sequence' by providing temporary data as specified in the
 * forgeXxx() calls thereby by-passing main.PrePortal and main.FirstLaunch mechanisms of initializing data.
 * These data are then used to build Dashboard letting the user see a runtime-instance of the system, yet
 * not verified.
 *
 * For testing of a specific functionality, use utg.SpecificTester instead.
 */
public class GeneralTester {

    public static void main(String[] args) {
        forgeDetailsForTest();
        forgeActiveCoursesForTest();
        forgeCoursesForTest();
        SwingUtilities.invokeLater(()-> new Board().setVisible(true));
    }

    /**
     * Set the user's details here.
     */
    private static void forgeDetailsForTest(){
        Student.setMonthOfAdmission(9);
        Student.setYearOfAdmission(2017);
        Student.setSemester("2019/2020 second semester".toUpperCase());
        Student.setFirstName("Jallow");
        Student.setLastName("Ismaila");
        Student.setMatNumber(22113377);
        Student.setMajor("Major");
        Student.setCGPA(3.954);
        Student.setProgram("Bachelor of Science in Mathematics");
        Student.setSchool("Arts and Sciences");
        Student.setDivision("Physical and Natural Sciences");
        Student.setAddress("Sukuta");
        Student.setTelephones("3413910");
        Student.setNationality("Gambia");
        Student.setDateOfBirth("01 Jan 1999");
        Student.setPortalMail("email@utg.edu.gm");
        Student.setPortalPassword("student@utg");
//        Student.setMaritalStatue("");
//        Student.setPlaceOfBirth("");
        Student.setLevel("Undergraduate".toUpperCase());
        Student.setState("Running".toUpperCase());
        //
        Board.postProcesses.add(()-> {
            Student.setMajorCode("MTH");
            Student.setMinor("Minor");
            Student.setMinorCode("");
        });
    }

    /**
     * Add registered courses to the STARTUP_REGISTRATIONS list.
     * These courses will automatically be verified-set as they'll be added through the reallyAdd()
     * See main.RunningCoursesGenerator for more info.
     *
     * Unless otherwise altered, addition must follow the convention:
     *  RunningCoursesGenerator.STARTUP_REGISTRATIONS.add(main.RunningCourse)
     */
    private static void forgeActiveCoursesForTest(){
        RunningCoursesGenerator.STARTUP_REGISTRATIONS.add(new RunningCourse("MTH310","Number Theory",
                "Murphy Egwe","Brikama","Chancery rom 3","","",true));
        RunningCoursesGenerator.STARTUP_REGISTRATIONS.add(new RunningCourse("CPS204","Database Systems 1",
                "Fred Sangol Uche","Kanifing","PB103","Fridays","11:00",false));
    }

    /**
     * Add your own start-up courses.
     * These courses will be treated as if they're from the portal based on the boolean value given to the
     * validity parameter.
     *
     * Unless otherwise altered, addition must follow the convention:
     *  ModulesHandler.STARTUP_COURSES.add(main.Course)
     */
    private static void forgeCoursesForTest(){
        //2016/17
        ModulesHandler.STARTUP_COURSES.add(new Course("2016/2017","First Semester","gel101","Use of English 1","","","","",68,3,"",true));
        ModulesHandler.STARTUP_COURSES.add(new Course("2016/2017","First Semester","mth002","Pre-Calculus","","","","",77,3,"",true));
        //2017/18
        ModulesHandler.STARTUP_COURSES.add(new Course("2017/2018","First Semester","mth207","Introduction to Proofs","","","","",96.2,3,"",true));
        ModulesHandler.STARTUP_COURSES.add(new Course("2017/2018","First Semester","mth203","Linear Algebra 1","","","","",72,3,"",true));
        ModulesHandler.STARTUP_COURSES.add(new Course("2017/2018","First Semester","iss102","Introduction to Islam and the History of Islamic Sciences","","","","",90,3,"",true));
        ModulesHandler.STARTUP_COURSES.add(new Course("2017/2018","First Semester","mth201","Intermediate Calculus 1","","","","",79,3,"",true));
        ModulesHandler.STARTUP_COURSES.add(new Course("2017/2018","First Semester","mth102","Calculus 2","","","","",87,3,"",true));
        ModulesHandler.STARTUP_COURSES.add(new Course("2017/2018","Second Semester","mth2022","Intermediate Calculus 2".toUpperCase(),"Murphy Egwe","","","",71,3,"",true));
        ModulesHandler.STARTUP_COURSES.add(new Course("2017/2018","Second Semester","mth2051","Differential Equations 1".toUpperCase(),"Keeba Bah","","","",85,3,"",true));
        ModulesHandler.STARTUP_COURSES.add(new Course("2017/2018","Second Semester","der122","General Chemistry 2","Anthony Adjivon","","","",76,3,"",true));
        ModulesHandler.STARTUP_COURSES.add(new Course("2017/2018","Second Semester","cps111","Introduction to Information Communication Technology","Ebrima Sarr","","","",70,3,"",true));
        ModulesHandler.STARTUP_COURSES.add(new Course("2017/2018","Second Semester","cps101","Computer Programming 1","Fred Sangol","","","",80,3,"",true));
        //...
    }

}

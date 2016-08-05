package controller;

import java.util.Scanner;
import java.util.*;

/**
 * Created by Abisheik on 7/14/2016.
 */
public class SchedulingMain {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Integer selectedOption = 0;

        System.out.println("Scheduling algorithm");
        System.out.println("~~~~~~~~~~~~~~~~~~~~");

        System.out.println("Please select any one of the following scheduling algorithm : 3");
        System.out.println("1) ASAP algorithm 2) ALAP algorithm 3) List_L algorithm ");
        try {
            selectedOption = scanner.nextInt();
            SchedulingController schedulingController = new SchedulingController();
            if ( selectedOption == 1 ) {
                schedulingController.readInputFileByAlgorithm("asap");
            }
            else if ( selectedOption == 2 ) {
                schedulingController.readInputFileByAlgorithm("alap");
            }
            else {
                schedulingController.readInputFileByAlgorithm("list_l");
            }
        }
        catch ( InputMismatchException me ){
            System.out.println("Please enter valid option.");
            System.exit(0);
        }
    }
}


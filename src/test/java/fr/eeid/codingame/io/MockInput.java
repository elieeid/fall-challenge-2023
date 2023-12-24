package fr.eeid.codingame.io;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class MockInput {

    public static Scanner mock(String inputData){
        return new Scanner(new ByteArrayInputStream(inputData.getBytes()));
    }

}

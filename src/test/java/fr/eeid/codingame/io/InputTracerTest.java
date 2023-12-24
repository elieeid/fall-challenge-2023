package fr.eeid.codingame.io;

import org.junit.jupiter.api.Test;

import fr.eeid.codingame.io.InputTracer;

import static org.assertj.core.api.Assertions.assertThat;

public class InputTracerTest {

    @Test
    public void allValuesWithBlankSeparator(){
        // Given: a collection of 3 values separated by a blank space
        InputTracer inputTracer = new InputTracer(MockInput.mock("val1 val2 val3\n"));

        // When: calling method nextLine()
        String[] values = inputTracer.nextLine();

        // Then: 3 values are parsed and returned
        assertThat(values).isEqualTo(new String[]{"val1", "val2", "val3"});
    }

    @Test
    public void allValuesAsInts(){
        // Given: a collection of 3 int values separated by a blank space
        InputTracer inputTracer = new InputTracer(MockInput.mock("1 2 3\n"));

        // When: calling method nextLineAsInts()
        int[] values = inputTracer.nextLineAsInts();

        // Then: 3 int values are parsed and returned
        assertThat(values).isEqualTo(new int[]{1, 2, 3});
    }

    @Test
    public void twoLines(){
        // Given: two lines of values separated by a blank space
        InputTracer inputTracer = new InputTracer(MockInput.mock("val11 val12\n" +
                "val21 val22\n"));

        // When: reading the two lines
        String[] line1 = inputTracer.nextLine();
        String[] line2 = inputTracer.nextLine();

        // Then: lines are read and parsed properly
        assertThat(line1).isEqualTo(new String[]{"val11", "val12"});
        assertThat(line2).isEqualTo(new String[]{"val21", "val22"});
    }

    @Test
    public void singleValueAsString(){
        // Given: a collection of 3 values separated by a blank space
        InputTracer inputTracer = new InputTracer(MockInput.mock("val1 val2 val3\n"));

        // When: reading line as a single string
        String line = inputTracer.nextLineAsSingleString();

        // Then: the line is returned as-is, without the trailing \n
        assertThat(line).isEqualTo("val1 val2 val3");
    }

    @Test
    public void singleIntValue(){
        // Given: a line with a single integer value
        InputTracer inputTracer = new InputTracer(MockInput.mock("10\n"));

        // When: reading line as a single integer
        int value = inputTracer.nextLineAsSingleInt();

        // Then: the integer value returned is correct
        assertThat(value).isEqualTo(10);
    }

    @Test
    public void traceEveryLineRead(){
        // Given: multiple lines, of mixed formats
        InputTracer inputTracer = new InputTracer(MockInput.mock("val1 val2 val3\n" +
                "val1 val2 val3\n" +
                "3\n" +
                "5 6 7\n"));

        // When: reading the 3 lines
        inputTracer.nextLine();
        inputTracer.nextLineAsSingleString();
        inputTracer.nextLineAsSingleInt();
        inputTracer.nextLineAsInts();

        // Then: the tracer contains all the read lines
        assertThat(inputTracer.trace()).isEqualTo("val1 val2 val3\\n" +
                "val1 val2 val3\\n" +
                "3\\n" +
                "5 6 7");
    }
}
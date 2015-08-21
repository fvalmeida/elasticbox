package org.fvalmeida.elasticbox;

import joptsimple.OptionParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;

/**
 * Created by fvalmeida on 8/21/15.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class PrintHelp {

    @Test
    public void original() throws IOException {
        System.out.println("ORIGINAL\n");
        OptionParser parser = Application.getOptionParse();
        parser.printHelpOn(System.out);
    }

    @Test
    public void custom() throws IOException {
        System.out.println("CUSTOM\n");
        Application.main(new String[]{"-h"});
    }

}


package com.manywho.services.box.unit;

import com.manywho.services.box.utilities.Base64Helper;
import org.junit.Assert;
import org.junit.Test;

public class Base64HelperTest {

    @Test
    public void testBase64EncodeDecode() {

        String text = "aa@--@@%6&*%$£\n,,.";
        String encoded = Base64Helper.encode(text);
        Assert.assertEquals(text, Base64Helper.decode(encoded));

    }
}